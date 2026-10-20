package com.democommerce.backend;

import com.democommerce.backend.tools.MockCrmProvider;
import com.democommerce.backend.tools.MockTelephonyProvider;
import com.democommerce.backend.tools.ToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolServiceTest {
    @Mock ConversationRepository conversations;
    @Mock SupportTicketRepository tickets;
    @Mock EscalationRepository escalations;
    @Mock ToolExecutionRepository executions;
    @Mock AuditLogRepository auditLogs;
    private ToolService service;

    @BeforeEach
    void setUp() {
        service = new ToolService(conversations, tickets, escalations, executions, auditLogs,
                new SensitiveDataRedactor(), new ObjectMapper(), new MockCrmProvider(), new MockTelephonyProvider());
    }

    @Test
    void orderLookupReturnsTomorrowAndWritesExecutionAudit() {
        Map<String, Object> output = service.execute(7L, null, "get_order_status", Map.of("orderId", "ORD-10001"));

        assertEquals("OUT_FOR_DELIVERY", output.get("status"));
        assertEquals(LocalDate.now().plusDays(1).toString(), output.get("expectedDelivery"));
        ArgumentCaptor<ToolExecution> captor = ArgumentCaptor.forClass(ToolExecution.class);
        verify(executions).save(captor.capture());
        assertEquals(7L, captor.getValue().tenantId);
        assertEquals("get_order_status", captor.getValue().toolName);
        assertTrue(captor.getValue().outputJson.contains("OUT_FOR_DELIVERY"));
    }

    @Test
    void ticketToolPersistsTicketAndExecutionWithTenantScope() {
        when(tickets.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticket = invocation.getArgument(0);
            ticket.id = 81L;
            return ticket;
        });
        Map<String, Object> result = service.execute(7L, null, "create_support_ticket",
                Map.of("subject", "Late delivery", "description", "Please help"));

        assertEquals(81L, result.get("ticketId"));
        ArgumentCaptor<SupportTicket> ticketCaptor = ArgumentCaptor.forClass(SupportTicket.class);
        verify(tickets).save(ticketCaptor.capture());
        assertEquals(7L, ticketCaptor.getValue().tenantId);
        assertEquals("OPEN", ticketCaptor.getValue().status);
        verify(executions).save(any(ToolExecution.class));
    }

    @Test
    void transferToolPersistsEscalationAndUpdatesConversation() {
        Conversation conversation = new Conversation();
        conversation.id = 44L;
        conversation.tenantId = 7L;
        conversation.summary = "Customer has a delivery issue";
        when(conversations.findByIdAndTenantId(44L, 7L)).thenReturn(Optional.of(conversation));
        when(escalations.save(any(Escalation.class))).thenAnswer(invocation -> {
            Escalation escalation = invocation.getArgument(0);
            escalation.id = 91L;
            return escalation;
        });

        Map<String, Object> result = service.execute(7L, 44L, "transfer_to_human", Map.of("reason", "Customer request"));

        assertEquals(91L, result.get("escalationId"));
        assertEquals("ESCALATED", conversation.status);
        ArgumentCaptor<Escalation> captor = ArgumentCaptor.forClass(Escalation.class);
        verify(escalations).save(captor.capture());
        assertEquals(7L, captor.getValue().tenantId);
        assertEquals(44L, captor.getValue().conversationId);
        verify(executions).save(any(ToolExecution.class));
    }
}
