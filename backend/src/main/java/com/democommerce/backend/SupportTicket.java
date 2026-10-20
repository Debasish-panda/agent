package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "conversation_id")
    public Long conversationId;
    @Column(name = "customer_id")
    public Long customerId;
    @Column(nullable = false, length = 240)
    public String subject;
    @Column(nullable = false, columnDefinition = "text")
    public String description;
    @Column(nullable = false, length = 40)
    public String status;
    @Column(nullable = false, length = 30)
    public String priority;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
