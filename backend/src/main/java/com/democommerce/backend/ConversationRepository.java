package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findAllByTenantIdOrderByUpdatedAtDesc(Long tenantId);
    Optional<Conversation> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantId(Long tenantId);
}
