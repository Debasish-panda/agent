package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByTenantIdOrderByCreatedAtDesc(Long tenantId);
    Optional<Customer> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Customer> findFirstByTenantIdAndEmailIgnoreCase(Long tenantId, String email);
    long countByTenantId(Long tenantId);
}
