package com.democommerce.backend;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MockAiProvider implements AiProvider {
    @Override
    public String respond(String customerMessage, List<String> relevantKnowledge, String toolResult) {
        if (toolResult != null) return toolResult;
        if (!relevantKnowledge.isEmpty()) {
            return "Here is what I found: " + relevantKnowledge.get(0);
        }
        return "Thanks for contacting Demo Commerce. I can help with orders, returns, and support requests. What would you like help with?";
    }
}
