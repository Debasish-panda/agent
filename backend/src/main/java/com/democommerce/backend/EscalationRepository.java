package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    List<Escalation> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId);
    long countByTenantId(Long tenantId);
}
