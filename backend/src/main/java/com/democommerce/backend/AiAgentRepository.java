package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAgentRepository extends JpaRepository<AiAgent, Long> {
    boolean existsByTenantIdAndName(Long tenantId, String name);
}
