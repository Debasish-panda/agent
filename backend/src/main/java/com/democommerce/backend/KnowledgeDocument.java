package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "knowledge_documents")
public class KnowledgeDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(nullable = false, length = 240)
    public String title;
    @Column(nullable = false, columnDefinition = "text")
    public String content;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
