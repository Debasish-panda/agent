package com.democommerce.backend;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tool_executions")
public class ToolExecution {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "tenant_id", nullable = false)
    public Long tenantId;
    @Column(name = "conversation_id")
    public Long conversationId;
    @Column(name = "tool_name", nullable = false, length = 80)
    public String toolName;
    @Column(name = "input_json", nullable = false, columnDefinition = "text")
    public String inputJson;
    @Column(name = "output_json", nullable = false, columnDefinition = "text")
    public String outputJson;
    @Column(nullable = false, length = 30)
    public String status;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;
}
