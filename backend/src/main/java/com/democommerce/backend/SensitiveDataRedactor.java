package com.democommerce.backend;

import org.springframework.stereotype.Component;

@Component
public class SensitiveDataRedactor {
    private static final String EMAIL = "(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b";
    private static final String PHONE = "(?<!\\w)(?:\\+?\\d[\\d .()\\-]{7,}\\d)(?!\\w)";
    private static final String CARD = "\\b(?:\\d[ -]*?){13,19}\\b";

    public String redact(String value) {
        if (value == null) return null;
        return value.replaceAll(CARD, "[REDACTED_CARD]")
                .replaceAll(EMAIL, "[REDACTED_EMAIL]")
                .replaceAll(PHONE, "[REDACTED_PHONE]");
    }
}
