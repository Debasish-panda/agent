package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {
    List<KnowledgeDocument> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId);
    Optional<KnowledgeDocument> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantId(Long tenantId);
}
