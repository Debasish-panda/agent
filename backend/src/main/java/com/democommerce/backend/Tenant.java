package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, length = 160)
    public String name;
    @Column(nullable = false, unique = true, length = 120)
    public String slug;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
