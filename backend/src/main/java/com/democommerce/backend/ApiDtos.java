package com.democommerce.backend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ApiDtos {
    private ApiDtos() {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record UserView(Long id, String email, String role, Long tenantId) {}
    public record LoginResponse(String token, UserView user) {}
    public record KnowledgeRequest(@NotBlank @Size(max = 240) String title, @NotBlank @Size(max = 30000) String content) {}
    public record KnowledgeView(Long id, String title, String content, Instant createdAt) {}
    public record SearchRequest(@NotBlank @Size(max = 500) String query) {}
    public record SearchResult(String query, List<KnowledgeView> results) {}
    public record CustomerRequest(@NotBlank @Size(max = 160) String name, @Email String email, @Size(max = 40) String phone) {}
    public record CustomerView(Long id, String name, String email, String phone, Instant createdAt) {}
    public record ConversationRequest(Long customerId, @Size(max = 160) String customerName, @Email String customerEmail,
                                      @Size(max = 40) String customerPhone, @Size(max = 40) String channel,
                                      @NotBlank @Size(max = 10000) String message) {}
    public record MessageRequest(@NotBlank @Size(max = 10000) String content) {}
    public record MessageView(Long id, String role, String content, String toolName, Instant createdAt) {}
    public record ConversationView(Long id, Long customerId, String customerName, String status, String channel,
                                   String summary, Instant createdAt, Instant updatedAt, List<MessageView> messages) {}
    public record ToolRequest(Long conversationId, @Size(max = 50) Map<String, Object> arguments) {}
    public record TicketView(Long id, Long conversationId, Long customerId, String subject, String description,
                             String status, String priority, Instant createdAt) {}
    public record EscalationView(Long id, Long conversationId, String reason, String status, String context, Instant createdAt) {}
    public record ToolExecutionView(Long id, Long conversationId, String toolName, String inputJson, String outputJson,
                                    String status, Instant createdAt) {}
    public record AnalyticsSummary(long conversations, long openTickets, long escalations, long toolExecutions,
                                   long customers, long knowledgeDocuments) {}
    public record HealthView(String status, String redis) {}
}
