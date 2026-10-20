package com.democommerce.backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SensitiveDataRedactorTest {
    private final SensitiveDataRedactor redactor = new SensitiveDataRedactor();

    @Test
    void redactsEmailPhoneAndCardLikeNumbers() {
        assertEquals("Contact [REDACTED_EMAIL] at [REDACTED_PHONE]; card [REDACTED_CARD].",
                redactor.redact("Contact alex@example.com at +1 (555) 123-4567; card 4111 1111 1111 1111."));
    }

    @Test
    void acceptsNullAndPreservesOrdinaryText() {
        assertNull(redactor.redact(null));
        assertEquals("Where is ORD-10001?", redactor.redact("Where is ORD-10001?"));
    }
}
