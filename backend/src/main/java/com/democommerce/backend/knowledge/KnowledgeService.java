package com.democommerce.backend.knowledge;

import com.democommerce.backend.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class KnowledgeService {
    private final KnowledgeDocumentRepository documents;
    private final KnowledgeChunkRepository chunks;
    private final SensitiveDataRedactor redactor;
    private final AuditLogRepository auditLogs;

    public KnowledgeService(KnowledgeDocumentRepository documents, KnowledgeChunkRepository chunks,
                            SensitiveDataRedactor redactor, AuditLogRepository auditLogs) {
        this.documents = documents;
        this.chunks = chunks;
        this.redactor = redactor;
        this.auditLogs = auditLogs;
    }

    @Transactional
    public ApiDtos.KnowledgeView create(Long tenantId, ApiDtos.KnowledgeRequest request, Long actorId) {
        KnowledgeDocument document = new KnowledgeDocument();
        document.tenantId = tenantId;
        document.title = redactor.redact(request.title());
        document.content = redactor.redact(request.content());
        document.createdAt = Instant.now();
        documents.save(document);
        saveChunks(document);
        audit(tenantId, actorId, "KNOWLEDGE_CREATED", document.id);
        return toView(document);
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.KnowledgeView> list(Long tenantId) {
        return documents.findAllByTenantIdOrderByCreatedAtDesc(tenantId).stream().map(this::toView).toList();
    }

    @Transactional
    public ApiDtos.KnowledgeView update(Long tenantId, Long documentId, ApiDtos.KnowledgeRequest request, Long actorId) {
        KnowledgeDocument document = findDocument(tenantId, documentId);
        document.title = redactor.redact(request.title());
        document.content = redactor.redact(request.content());
        documents.save(document);
        chunks.deleteAllByTenantIdAndDocumentId(tenantId, document.id);
        saveChunks(document);
        audit(tenantId, actorId, "KNOWLEDGE_UPDATED", document.id);
        return toView(document);
    }

    @Transactional
    public void delete(Long tenantId, Long documentId, Long actorId) {
        KnowledgeDocument document = findDocument(tenantId, documentId);
        documents.delete(document);
        audit(tenantId, actorId, "KNOWLEDGE_DELETED", document.id);
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.KnowledgeView> search(Long tenantId, String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        Set<Long> matchingDocumentIds = new HashSet<>();
        chunks.findAllByTenantIdAndContentContainingIgnoreCase(tenantId, query)
                .forEach(chunk -> matchingDocumentIds.add(chunk.documentId));
        return documents.findAllByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(document -> matchingDocumentIds.contains(document.id)
                        || document.title.toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing((KnowledgeDocument document) ->
                        (document.title + " " + document.content).toLowerCase(Locale.ROOT).indexOf(normalized)))
                .limit(10).map(this::toView).toList();
    }

    private ApiDtos.KnowledgeView toView(KnowledgeDocument document) {
        return new ApiDtos.KnowledgeView(document.id, redactor.redact(document.title), redactor.redact(document.content), document.createdAt);
    }

    private KnowledgeDocument findDocument(Long tenantId, Long documentId) {
        return documents.findByIdAndTenantId(documentId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge document not found"));
    }

    private void audit(Long tenantId, Long actorId, String action, Long documentId) {
        AuditLog audit = new AuditLog();
        audit.tenantId = tenantId;
        audit.actorUserId = actorId;
        audit.action = action;
        audit.entityType = "knowledge_document";
        audit.entityId = documentId;
        audit.createdAt = Instant.now();
        auditLogs.save(audit);
    }

    private void saveChunks(KnowledgeDocument document) {
        int start = 0;
        int index = 0;
        while (start < document.content.length()) {
            int end = Math.min(start + 1000, document.content.length());
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.tenantId = document.tenantId;
            chunk.documentId = document.id;
            chunk.content = document.content.substring(start, end);
            chunk.chunkIndex = index++;
            chunk.createdAt = Instant.now();
            chunks.save(chunk);
            if (end == document.content.length()) break;
            start = end - 120;
        }
    }
}
