package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "customers", indexes = @Index(name = "idx_customers_tenant_created", columnList = "tenant_id, created_at"))
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(nullable = false, length = 160)
    public String name;
    @Column(length = 254)
    public String email;
    @Column(length = 40)
    public String phone;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
