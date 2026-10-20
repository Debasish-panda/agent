package com.democommerce.backend;

import java.util.List;

public interface AiProvider {
    String respond(String customerMessage, List<String> relevantKnowledge, String toolResult);
}
