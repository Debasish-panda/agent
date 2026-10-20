package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "document_id", nullable = false)
    public Long documentId;
    @Column(nullable = false, columnDefinition = "text")
    public String content;
    @Column(name = "chunk_index", nullable = false)
    public int chunkIndex;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
