package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "actor_user_id")
    public Long actorUserId;
    @Column(nullable = false, length = 100)
    public String action;
    @Column(name = "entity_type", nullable = false, length = 100)
    public String entityType;
    @Column(name = "entity_id")
    public Long entityId;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
