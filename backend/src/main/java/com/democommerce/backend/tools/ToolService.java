package com.democommerce.backend.tools;

import com.democommerce.backend.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ToolService {
    private final ConversationRepository conversations;
    private final SupportTicketRepository tickets;
    private final EscalationRepository escalations;
    private final ToolExecutionRepository executions;
    private final AuditLogRepository auditLogs;
    private final SensitiveDataRedactor redactor;
    private final ObjectMapper mapper;
    private final CrmProvider crmProvider;
    private final TelephonyProvider telephonyProvider;

    public ToolService(ConversationRepository conversations, SupportTicketRepository tickets,
                       EscalationRepository escalations, ToolExecutionRepository executions,
                       AuditLogRepository auditLogs, SensitiveDataRedactor redactor, ObjectMapper mapper,
                       CrmProvider crmProvider, TelephonyProvider telephonyProvider) {
        this.conversations = conversations;
        this.tickets = tickets;
        this.escalations = escalations;
        this.executions = executions;
        this.auditLogs = auditLogs;
        this.redactor = redactor;
        this.mapper = mapper;
        this.crmProvider = crmProvider;
        this.telephonyProvider = telephonyProvider;
    }

    @Transactional
    public Map<String, Object> execute(Long tenantId, Long conversationId, String toolName, Map<String, Object> arguments) {
        Map<String, Object> input = arguments == null ? new LinkedHashMap<>() : new LinkedHashMap<>(arguments);
        Conversation conversation = null;
        if (conversationId != null) {
            conversation = conversations.findByIdAndTenantId(conversationId, tenantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        }
        Map<String, Object> output;
        switch (toolName) {
            case "get_order_status" -> output = orderStatus(input);
            case "create_support_ticket" -> output = createTicket(tenantId, conversation, input);
            case "transfer_to_human" -> output = transfer(tenantId, conversation, input);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported tool");
        }
        persistExecution(tenantId, conversationId, toolName, input, output);
        return output;
    }

    private Map<String, Object> orderStatus(Map<String, Object> args) {
        String orderId = String.valueOf(args.getOrDefault("orderId", args.getOrDefault("order_id", ""))).trim();
        CrmProvider.OrderStatus status = crmProvider.getOrderStatus(orderId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", status.orderId());
        result.put("status", status.status());
        if (status.expectedDelivery() != null) result.put("expectedDelivery", status.expectedDelivery().toString());
        result.put("message", status.message());
        return result;
    }

    private Map<String, Object> createTicket(Long tenantId, Conversation conversation, Map<String, Object> args) {
        String subject = redactor.redact(stringArg(args, "subject", "Customer support request"));
        String description = redactor.redact(stringArg(args, "description", "Customer requested assistance."));
        String priority = stringArg(args, "priority", "NORMAL").toUpperCase();
        if (!("LOW".equals(priority) || "NORMAL".equals(priority) || "HIGH".equals(priority))) priority = "NORMAL";
        SupportTicket ticket = new SupportTicket();
        ticket.tenantId = tenantId;
        ticket.conversationId = conversation == null ? null : conversation.id;
        ticket.customerId = conversation == null ? null : conversation.customerId;
        ticket.subject = subject.length() > 240 ? subject.substring(0, 240) : subject;
        ticket.description = description;
        ticket.status = "OPEN";
        ticket.priority = priority;
        ticket.createdAt = Instant.now();
        tickets.save(ticket);
        saveAudit(tenantId, "SUPPORT_TICKET_CREATED", "support_ticket", ticket.id);
        return Map.of("ticketId", ticket.id, "status", ticket.status, "subject", ticket.subject,
                "message", "I've created support ticket #" + ticket.id + " for your request.");
    }

    private Map<String, Object> transfer(Long tenantId, Conversation conversation, Map<String, Object> args) {
        if (conversation == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conversation is required for escalation");
        String transferStatus = telephonyProvider.transferToHuman(conversation.id);
        Escalation escalation = new Escalation();
        escalation.tenantId = tenantId;
        escalation.conversationId = conversation.id;
        escalation.reason = redactor.redact(stringArg(args, "reason", "Customer requested a human representative."));
        escalation.context = redactor.redact(stringArg(args, "context", conversation.summary == null ? "" : conversation.summary));
        escalation.status = transferStatus;
        escalation.createdAt = Instant.now();
        escalations.save(escalation);
        conversation.status = "ESCALATED";
        conversation.updatedAt = Instant.now();
        conversations.save(conversation);
        saveAudit(tenantId, "CONVERSATION_ESCALATED", "escalation", escalation.id);
        return Map.of("escalationId", escalation.id, "status", escalation.status,
                "message", "I'm transferring you to a human support representative now.");
    }

    private String stringArg(Map<String, Object> args, String key, String fallback) {
        Object value = args.get(key);
        return value == null || value.toString().isBlank() ? fallback : value.toString().trim();
    }

    private void persistExecution(Long tenantId, Long conversationId, String tool, Map<String, Object> input,
                                  Map<String, Object> output) {
        ToolExecution execution = new ToolExecution();
        execution.tenantId = tenantId;
        execution.conversationId = conversationId;
        execution.toolName = tool;
        execution.inputJson = json(redactMap(input));
        execution.outputJson = json(redactMap(output));
        execution.status = "SUCCEEDED";
        execution.createdAt = Instant.now();
        executions.save(execution);
    }

    private Map<String, Object> redactMap(Map<String, Object> source) {
        Map<String, Object> safe = new LinkedHashMap<>();
        source.forEach((key, value) -> safe.put(key, redactValue(value)));
        return safe;
    }

    private Object redactValue(Object value) {
        if (value instanceof String text) return redactor.redact(text);
        if (value instanceof Number number) return redactor.redact(number.toString());
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> safe = new LinkedHashMap<>();
            map.forEach((key, nested) -> safe.put(String.valueOf(key), redactValue(nested)));
            return safe;
        }
        if (value instanceof Iterable<?> values) {
            java.util.List<Object> safe = new java.util.ArrayList<>();
            values.forEach(item -> safe.add(redactValue(item)));
            return safe;
        }
        return value;
    }

    private String json(Object object) {
        try { return mapper.writeValueAsString(object); }
        catch (JsonProcessingException exception) { throw new IllegalStateException("Unable to audit tool execution", exception); }
    }

    private void saveAudit(Long tenantId, String action, String type, Long entityId) {
        AuditLog audit = new AuditLog();
        audit.tenantId = tenantId;
        audit.action = action;
        audit.entityType = type;
        audit.entityId = entityId;
        audit.createdAt = Instant.now();
        auditLogs.save(audit);
    }
}
