package com.democommerce.backend.api;

import com.democommerce.backend.*;
import com.democommerce.backend.tools.ToolService;
import jakarta.validation.Valid;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class WorkspaceController {
    private final ConversationRepository conversations;
    private final SupportTicketRepository tickets;
    private final EscalationRepository escalations;
    private final ToolExecutionRepository executions;
    private final CustomerRepository customers;
    private final KnowledgeDocumentRepository documents;
    private final RedisConnectionFactory redis;
    private final ToolService tools;

    public WorkspaceController(ConversationRepository conversations, SupportTicketRepository tickets,
                               EscalationRepository escalations, ToolExecutionRepository executions,
                               CustomerRepository customers, KnowledgeDocumentRepository documents,
                               RedisConnectionFactory redis, ToolService tools) {
        this.conversations = conversations;
        this.tickets = tickets;
        this.escalations = escalations;
        this.executions = executions;
        this.customers = customers;
        this.documents = documents;
        this.redis = redis;
        this.tools = tools;
    }

    @GetMapping({"/api/tickets", "/api/support-tickets"})
    public List<ApiDtos.TicketView> tickets(@AuthenticationPrincipal AppUser user) {
        return tickets.findAllByTenantIdOrderByCreatedAtDesc(user.tenantId).stream()
                .map(ticket -> new ApiDtos.TicketView(ticket.id, ticket.conversationId, ticket.customerId,
                        ticket.subject, ticket.description, ticket.status, ticket.priority, ticket.createdAt)).toList();
    }

    @GetMapping("/api/escalations")
    public List<ApiDtos.EscalationView> escalations(@AuthenticationPrincipal AppUser user) {
        return escalations.findAllByTenantIdOrderByCreatedAtDesc(user.tenantId).stream()
                .map(item -> new ApiDtos.EscalationView(item.id, item.conversationId, item.reason, item.status, item.context, item.createdAt)).toList();
    }

    @GetMapping("/api/tools/executions")
    public List<ApiDtos.ToolExecutionView> executions(@AuthenticationPrincipal AppUser user) {
        return executions.findAllByTenantIdOrderByCreatedAtDesc(user.tenantId).stream()
                .map(item -> new ApiDtos.ToolExecutionView(item.id, item.conversationId, item.toolName,
                        item.inputJson, item.outputJson, item.status, item.createdAt)).toList();
    }

    @GetMapping("/api/analytics/summary")
    public ApiDtos.AnalyticsSummary summary(@AuthenticationPrincipal AppUser user) {
        return new ApiDtos.AnalyticsSummary(conversations.countByTenantId(user.tenantId),
                tickets.countByTenantIdAndStatus(user.tenantId, "OPEN"), escalations.countByTenantId(user.tenantId),
                executions.countByTenantId(user.tenantId), customers.countByTenantId(user.tenantId),
                documents.countByTenantId(user.tenantId));
    }

    @PostMapping("/api/tools/{toolName}")
    @ResponseStatus(HttpStatus.OK)
    public Object executeTool(@AuthenticationPrincipal AppUser user, @PathVariable String toolName,
                              @Valid @RequestBody(required = false) ApiDtos.ToolRequest request) {
        Long conversationId = request == null ? null : request.conversationId();
        return tools.execute(user.tenantId, conversationId, toolName,
                request == null ? null : request.arguments());
    }

    @GetMapping("/api/system/health")
    public ApiDtos.HealthView health() {
        String redisStatus;
        RedisConnection connection = null;
        try {
            connection = redis.getConnection();
            redisStatus = "PONG".equalsIgnoreCase(connection.ping()) ? "UP" : "DOWN";
        } catch (RuntimeException exception) {
            redisStatus = "DOWN";
        } finally {
            if (connection != null) connection.close();
        }
        return new ApiDtos.HealthView("UP", redisStatus);
    }
}
