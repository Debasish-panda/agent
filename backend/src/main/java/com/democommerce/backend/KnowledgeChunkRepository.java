package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, Long> {
    List<KnowledgeChunk> findAllByTenantIdAndContentContainingIgnoreCase(Long tenantId, String content);
    void deleteAllByTenantIdAndDocumentId(Long tenantId, Long documentId);
}
