package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ai_agents", indexes = @Index(name = "idx_ai_agents_tenant", columnList = "tenant_id"))
public class AiAgent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(nullable = false, length = 160)
    public String name;
    @Column(nullable = false, length = 40)
    public String provider;
    @Column(nullable = false)
    public boolean active = true;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
