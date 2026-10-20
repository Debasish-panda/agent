package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversation_messages")
public class ConversationMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "conversation_id", nullable = false)
    public Long conversationId;
    @Column(nullable = false, length = 30)
    public String role;
    @Column(nullable = false, columnDefinition = "text")
    public String content;
    @Column(name = "tool_name", length = 80)
    public String toolName;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
