package com.democommerce.backend;

import com.democommerce.backend.knowledge.KnowledgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {
    @Mock KnowledgeDocumentRepository documents;
    @Mock KnowledgeChunkRepository chunks;
    @Mock AuditLogRepository auditLogs;

    @Test
    void redactsKnowledgeBeforePersistingAndStoresTenantScopedChunks() {
        when(documents.save(any(KnowledgeDocument.class))).thenAnswer(invocation -> {
            KnowledgeDocument document = invocation.getArgument(0);
            document.id = 5L;
            return document;
        });
        KnowledgeService service = new KnowledgeService(documents, chunks, new SensitiveDataRedactor(), auditLogs);

        ApiDtos.KnowledgeView view = service.create(8L,
                new ApiDtos.KnowledgeRequest("Contact alex@example.com", "Call +1 (555) 123-4567 for help."), 9L);

        assertEquals("Contact [REDACTED_EMAIL]", view.title());
        assertFalse(view.content().contains("555"));
        ArgumentCaptor<KnowledgeDocument> documentCaptor = ArgumentCaptor.forClass(KnowledgeDocument.class);
        verify(documents).save(documentCaptor.capture());
        assertEquals(8L, documentCaptor.getValue().tenantId);
        assertFalse(documentCaptor.getValue().content.contains("555"));
        ArgumentCaptor<KnowledgeChunk> chunkCaptor = ArgumentCaptor.forClass(KnowledgeChunk.class);
        verify(chunks).save(chunkCaptor.capture());
        assertEquals(8L, chunkCaptor.getValue().tenantId);
        assertEquals(5L, chunkCaptor.getValue().documentId);
    }

    @Test
    void updateReplacesTenantScopedChunksAndRedactsContent() {
        KnowledgeDocument document = new KnowledgeDocument();
        document.id = 5L;
        document.tenantId = 8L;
        document.title = "Old title";
        document.content = "Old content";
        document.createdAt = Instant.now();
        when(documents.findByIdAndTenantId(5L, 8L)).thenReturn(Optional.of(document));
        when(documents.save(any(KnowledgeDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        KnowledgeService service = new KnowledgeService(documents, chunks, new SensitiveDataRedactor(), auditLogs);

        ApiDtos.KnowledgeView view = service.update(8L, 5L,
                new ApiDtos.KnowledgeRequest("Contact alex@example.com", "Updated help content"), 9L);

        assertEquals("Contact [REDACTED_EMAIL]", view.title());
        assertEquals("Updated help content", view.content());
        verify(chunks).deleteAllByTenantIdAndDocumentId(8L, 5L);
        verify(chunks).save(any(KnowledgeChunk.class));
        verify(auditLogs).save(argThat(audit -> "KNOWLEDGE_UPDATED".equals(audit.action)));
    }

    @Test
    void updateCannotAccessDocumentFromAnotherTenant() {
        when(documents.findByIdAndTenantId(5L, 8L)).thenReturn(Optional.empty());
        KnowledgeService service = new KnowledgeService(documents, chunks, new SensitiveDataRedactor(), auditLogs);

        assertThrows(ResponseStatusException.class, () -> service.update(8L, 5L,
                new ApiDtos.KnowledgeRequest("Title", "Content"), 9L));
        verify(documents, never()).save(any(KnowledgeDocument.class));
        verify(chunks, never()).deleteAllByTenantIdAndDocumentId(any(), any());
    }
}
