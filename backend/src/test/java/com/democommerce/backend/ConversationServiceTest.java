package com.democommerce.backend;

import com.democommerce.backend.conversations.ConversationService;
import com.democommerce.backend.knowledge.KnowledgeService;
import com.democommerce.backend.tools.ToolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {
    @Mock ConversationRepository conversations;
    @Mock ConversationMessageRepository messages;
    @Mock CustomerRepository customers;
    @Mock KnowledgeService knowledge;
    @Mock ToolService tools;
    private ConversationService service;

    @BeforeEach
    void setUp() {
        service = new ConversationService(conversations, messages, customers, knowledge, tools,
                new MockAiProvider(), new SensitiveDataRedactor());
        lenient().when(conversations.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            conversation.id = 12L;
            return conversation;
        });
        lenient().when(customers.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.id = 3L;
            return customer;
        });
        lenient().when(knowledge.search(anyLong(), anyString())).thenReturn(List.of());
    }

    @Test
    void createsTenantScopedConversationAndPersistsToolAiTranscript() {
        when(tools.execute(eq(7L), eq(12L), eq("get_order_status"), anyMap()))
                .thenReturn(Map.of("message", "Order ORD-10001 is out for delivery and expected tomorrow."));
        when(messages.findAllByTenantIdAndConversationIdOrderByCreatedAtAsc(7L, 12L)).thenReturn(List.of());

        ApiDtos.ConversationView response = service.create(7L, new ApiDtos.ConversationRequest(
                null, "Demo Customer", null, null, "WEB", "Where is my order ORD-10001?"));

        assertEquals(12L, response.id());
        assertEquals(3L, response.customerId());
        assertEquals("OPEN", response.status());
        verify(tools).execute(eq(7L), eq(12L), eq("get_order_status"), anyMap());
        ArgumentCaptor<ConversationMessage> captor = ArgumentCaptor.forClass(ConversationMessage.class);
        verify(messages, times(3)).save(captor.capture());
        assertEquals(List.of("USER", "TOOL", "ASSISTANT"),
                captor.getAllValues().stream().map(message -> message.role).toList());
        assertTrue(captor.getAllValues().get(2).content.contains("expected tomorrow"));
    }

    @Test
    void rejectsCustomerFromAnotherTenant() {
        when(customers.findByIdAndTenantId(99L, 7L)).thenReturn(Optional.empty());
        ApiDtos.ConversationRequest request = new ApiDtos.ConversationRequest(
                99L, null, null, null, "WEB", "Hello");
        var exception = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.create(7L, request));
        assertEquals(404, exception.getStatusCode().value());
        verify(conversations, never()).save(any(Conversation.class));
    }
}
