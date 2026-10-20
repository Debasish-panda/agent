package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "customer_id")
    public Long customerId;
    @Column(nullable = false, length = 40)
    public String status;
    @Column(nullable = false, length = 40)
    public String channel;
    @Column(columnDefinition = "text")
    public String summary;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}
