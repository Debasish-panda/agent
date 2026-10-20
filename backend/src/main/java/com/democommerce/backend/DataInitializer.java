package com.democommerce.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;

@Component
public class DataInitializer implements CommandLineRunner {
    private final TenantRepository tenants;
    private final UserRepository users;
    private final CustomerRepository customers;
    private final AiAgentRepository agents;
    private final PasswordEncoder encoder;
    private final TransactionTemplate transactions;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(TenantRepository tenants, UserRepository users, CustomerRepository customers, AiAgentRepository agents,
                          PasswordEncoder encoder, TransactionTemplate transactions,
                          @Value("${app.demo.admin-email}") String adminEmail,
                          @Value("${app.demo.admin-password}") String adminPassword) {
        this.tenants = tenants;
        this.users = users;
        this.customers = customers;
        this.agents = agents;
        this.encoder = encoder;
        this.transactions = transactions;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        transactions.executeWithoutResult(status -> {
            Tenant tenant = tenants.findBySlug("demo-commerce").orElseGet(() -> {
                Tenant created = new Tenant();
                created.name = "Demo Commerce";
                created.slug = "demo-commerce";
                created.createdAt = Instant.now();
                return tenants.save(created);
            });
            if (!users.existsByEmailIgnoreCase(adminEmail)) {
                AppUser admin = new AppUser();
                admin.tenantId = tenant.id;
                admin.email = adminEmail;
                admin.passwordHash = encoder.encode(adminPassword);
                admin.role = "TENANT_ADMIN";
                admin.enabled = true;
                admin.createdAt = Instant.now();
                users.save(admin);
            }
            if (customers.findFirstByTenantIdAndEmailIgnoreCase(tenant.id, "alex@example.com").isEmpty()) {
                Customer customer = new Customer();
                customer.tenantId = tenant.id;
                customer.name = "Alex Morgan";
                customer.email = "alex@example.com";
                customer.phone = "+1-555-0101";
                customer.createdAt = Instant.now();
                customers.save(customer);
            }
            if (!agents.existsByTenantIdAndName(tenant.id, "Demo Assistant")) {
                AiAgent agent = new AiAgent();
                agent.tenantId = tenant.id;
                agent.name = "Demo Assistant";
                agent.provider = "mock";
                agent.active = true;
                agent.createdAt = Instant.now();
                agents.save(agent);
            }
        });
    }
}
