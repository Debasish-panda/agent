package com.democommerce.backend.conversations;

import com.democommerce.backend.*;
import com.democommerce.backend.knowledge.KnowledgeService;
import com.democommerce.backend.tools.ToolService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConversationService {
    private static final Pattern ORDER_ID = Pattern.compile("\\bORD-[A-Z0-9-]+\\b", Pattern.CASE_INSENSITIVE);
    private final ConversationRepository conversations;
    private final ConversationMessageRepository messages;
    private final CustomerRepository customers;
    private final KnowledgeService knowledge;
    private final ToolService tools;
    private final AiProvider aiProvider;
    private final SensitiveDataRedactor redactor;

    public ConversationService(ConversationRepository conversations, ConversationMessageRepository messages,
                               CustomerRepository customers, KnowledgeService knowledge, ToolService tools,
                               AiProvider aiProvider, SensitiveDataRedactor redactor) {
        this.conversations = conversations;
        this.messages = messages;
        this.customers = customers;
        this.knowledge = knowledge;
        this.tools = tools;
        this.aiProvider = aiProvider;
        this.redactor = redactor;
    }

    @Transactional
    public ApiDtos.ConversationView create(Long tenantId, ApiDtos.ConversationRequest request) {
        Customer customer;
        if (request.customerId() != null) {
            customer = customers.findByIdAndTenantId(request.customerId(), tenantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        } else {
            customer = new Customer();
            customer.tenantId = tenantId;
            customer.name = request.customerName() == null || request.customerName().isBlank()
                    ? "Guest Customer" : redactor.redact(request.customerName().trim());
            customer.email = request.customerEmail();
            customer.phone = request.customerPhone();
            customer.createdAt = Instant.now();
            customer = customers.save(customer);
        }
        Conversation conversation = new Conversation();
        conversation.tenantId = tenantId;
        conversation.customerId = customer.id;
        conversation.status = "OPEN";
        conversation.channel = request.channel() == null || request.channel().isBlank() ? "WEB" : request.channel().trim().toUpperCase();
        conversation.summary = abbreviate(redactor.redact(request.message()), 500);
        conversation.createdAt = Instant.now();
        conversation.updatedAt = conversation.createdAt;
        conversation = conversations.save(conversation);
        addTurn(tenantId, conversation, request.message());
        return toView(conversation, customer, true);
    }

    @Transactional
    public ApiDtos.ConversationView addMessage(Long tenantId, Long conversationId, String content) {
        Conversation conversation = getConversation(tenantId, conversationId);
        if ("CLOSED".equals(conversation.status)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conversation is closed");
        }
        conversation.updatedAt = Instant.now();
        conversations.save(conversation);
        addTurn(tenantId, conversation, content);
        Customer customer = customers.findByIdAndTenantId(conversation.customerId, tenantId).orElse(null);
        return toView(conversation, customer, true);
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.ConversationView> list(Long tenantId) {
        return conversations.findAllByTenantIdOrderByUpdatedAtDesc(tenantId).stream()
                .map(conversation -> toView(conversation,
                        customers.findByIdAndTenantId(conversation.customerId, tenantId).orElse(null), false))
                .toList();
    }

    @Transactional(readOnly = true)
    public ApiDtos.ConversationView get(Long tenantId, Long conversationId) {
        Conversation conversation = getConversation(tenantId, conversationId);
        Customer customer = customers.findByIdAndTenantId(conversation.customerId, tenantId).orElse(null);
        return toView(conversation, customer, true);
    }

    private void addTurn(Long tenantId, Conversation conversation, String originalMessage) {
        conversation.summary = abbreviate(redactor.redact(originalMessage), 500);
        saveMessage(tenantId, conversation.id, "USER", redactor.redact(originalMessage), null);
        ToolCall call = detectTool(originalMessage);
        String toolResult = null;
        if (call != null) {
            Map<String, Object> output = tools.execute(tenantId, conversation.id, call.name(), call.arguments());
            toolResult = String.valueOf(output.get("message"));
            saveMessage(tenantId, conversation.id, "TOOL", redactor.redact(toolResult), call.name());
        }
        List<String> relevant = knowledge.search(tenantId, originalMessage).stream()
                .limit(3).map(ApiDtos.KnowledgeView::content).toList();
        String answer = aiProvider.respond(originalMessage, relevant, toolResult);
        saveMessage(tenantId, conversation.id, "ASSISTANT", redactor.redact(answer), null);
        conversation.updatedAt = Instant.now();
        conversations.save(conversation);
    }

    private ToolCall detectTool(String content) {
        String lower = content.toLowerCase();
        if (lower.contains("speak with") || lower.contains("talk to") || lower.contains("human")
                || lower.contains("representative") || lower.contains("real person")) {
            return new ToolCall("transfer_to_human", Map.of(
                    "reason", "Customer requested a human representative",
                    "context", redactor.redact(content)));
        }
        if (lower.contains("complain") || lower.contains("complaint") || lower.contains("delay")
                || lower.contains("problem") || lower.contains("issue")) {
            return new ToolCall("create_support_ticket", Map.of(
                    "subject", "Customer support request",
                    "description", redactor.redact(content),
                    "priority", "NORMAL"));
        }
        Matcher matcher = ORDER_ID.matcher(content);
        if (matcher.find()) return new ToolCall("get_order_status", Map.of("orderId", matcher.group().toUpperCase()));
        return null;
    }

    private void saveMessage(Long tenantId, Long conversationId, String role, String content, String toolName) {
        ConversationMessage message = new ConversationMessage();
        message.tenantId = tenantId;
        message.conversationId = conversationId;
        message.role = role;
        message.content = content;
        message.toolName = toolName;
        message.createdAt = Instant.now();
        messages.save(message);
    }

    private ApiDtos.ConversationView toView(Conversation conversation, Customer customer, boolean includeMessages) {
        List<ApiDtos.MessageView> transcript = includeMessages
                ? messages.findAllByTenantIdAndConversationIdOrderByCreatedAtAsc(conversation.tenantId, conversation.id)
                    .stream().map(message -> new ApiDtos.MessageView(message.id, message.role,
                            redactor.redact(message.content), message.toolName, message.createdAt)).toList()
                : List.of();
        return new ApiDtos.ConversationView(conversation.id, conversation.customerId,
                customer == null ? null : customer.name, conversation.status, conversation.channel,
                redactor.redact(conversation.summary), conversation.createdAt, conversation.updatedAt, transcript);
    }

    private Conversation getConversation(Long tenantId, Long conversationId) {
        return conversations.findByIdAndTenantId(conversationId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
    }

    private String abbreviate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }

    private record ToolCall(String name, Map<String, Object> arguments) {}
}
