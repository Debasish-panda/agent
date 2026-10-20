package com.democommerce.backend.conversations;

import com.democommerce.backend.ApiDtos;
import com.democommerce.backend.AppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversations;
    public ConversationController(ConversationService conversations) { this.conversations = conversations; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.ConversationView create(@AuthenticationPrincipal AppUser user, @Valid @RequestBody ApiDtos.ConversationRequest request) {
        return conversations.create(user.tenantId, request);
    }

    @GetMapping
    public List<ApiDtos.ConversationView> list(@AuthenticationPrincipal AppUser user) {
        return conversations.list(user.tenantId);
    }

    @GetMapping("/{id}")
    public ApiDtos.ConversationView get(@AuthenticationPrincipal AppUser user, @PathVariable Long id) {
        return conversations.get(user.tenantId, id);
    }

    @PostMapping("/{id}/messages")
    public ApiDtos.ConversationView addMessage(@AuthenticationPrincipal AppUser user, @PathVariable Long id,
                                                @Valid @RequestBody ApiDtos.MessageRequest request) {
        return conversations.addMessage(user.tenantId, id, request.content());
    }
}
