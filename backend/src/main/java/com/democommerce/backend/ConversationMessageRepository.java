package com.democommerce.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    List<ConversationMessage> findAllByTenantIdAndConversationIdOrderByCreatedAtAsc(Long tenantId, Long conversationId);
}
