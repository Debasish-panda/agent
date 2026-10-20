package com.democommerce.backend.tools;

import org.springframework.stereotype.Component;

@Component
public class MockTelephonyProvider implements TelephonyProvider {
    @Override
    public String transferToHuman(Long conversationId) {
        if (conversationId == null) throw new IllegalArgumentException("Conversation ID is required for transfer");
        return "PENDING";
    }
}
