package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId);
    long countByTenantIdAndStatus(Long tenantId, String status);
}
