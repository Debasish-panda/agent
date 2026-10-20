package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "escalations")
public class Escalation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "conversation_id", nullable = false)
    public Long conversationId;
    @Column(nullable = false, columnDefinition = "text")
    public String reason;
    @Column(nullable = false, length = 40)
    public String status;
    @Column(nullable = false, columnDefinition = "text")
    public String context;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
