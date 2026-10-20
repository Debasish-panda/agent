package com.democommerce.backend.knowledge;

import com.democommerce.backend.ApiDtos;
import com.democommerce.backend.AppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledge;
    public KnowledgeController(KnowledgeService knowledge) { this.knowledge = knowledge; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiDtos.KnowledgeView create(@AuthenticationPrincipal AppUser user, @Valid @RequestBody ApiDtos.KnowledgeRequest request) {
        return knowledge.create(user.tenantId, request, user.id);
    }

    @GetMapping
    public List<ApiDtos.KnowledgeView> list(@AuthenticationPrincipal AppUser user) {
        return knowledge.list(user.tenantId);
    }

    @PutMapping("/{id}")
    public ApiDtos.KnowledgeView update(@AuthenticationPrincipal AppUser user, @PathVariable Long id,
                                        @Valid @RequestBody ApiDtos.KnowledgeRequest request) {
        return knowledge.update(user.tenantId, id, request, user.id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AppUser user, @PathVariable Long id) {
        knowledge.delete(user.tenantId, id, user.id);
    }

    @PostMapping("/search")
    public ApiDtos.SearchResult search(@AuthenticationPrincipal AppUser user, @Valid @RequestBody ApiDtos.SearchRequest request) {
        return new ApiDtos.SearchResult(request.query(), knowledge.search(user.tenantId, request.query()));
    }
}
