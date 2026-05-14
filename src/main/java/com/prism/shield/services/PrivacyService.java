package com.prism.shield.services;

import com.prism.shield.entities.PrivacyResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PrivacyService {

    private final ChatClient chatClient;
    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";

    public PrivacyService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public PrivacyResponse checkPrivacy(String userInput) {
        // 1. Get AI's findings using a "Constraint-First" prompt
        List<String> aiFoundPii = callAiForList(userInput);

        // 2. Hybrid Safety Net: Always run Regex for high-accuracy items (Emails)
        Set<String> finalPiiSet = new HashSet<>(aiFoundPii);
        Matcher matcher = Pattern.compile(EMAIL_REGEX).matcher(userInput);
        while (matcher.find()) {
            finalPiiSet.add(matcher.group());
        }

        // 3. Clean the list (remove nulls/blanks)
        List<String> cleanPiiList = finalPiiSet.stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toList());

        // 4. Perform Redaction
        String redactedText = redactLogic(userInput, cleanPiiList);

        return new PrivacyResponse(
                cleanPiiList,
                cleanPiiList.isEmpty() ? "SAFE" : "FLAGGED",
                redactedText);
    }

    private List<String> callAiForList(String input) {
        String systemPrompt = """
            ROLE: Technical Text Annotator.
            TASK: Identify all Proper Nouns (Names, Organizations, Cities) and contact strings in the text.
            FORMAT: Output ONLY the identified words as a comma-separated list.
            CONSTRAINT: Do not provide an introduction or an apology. If no proper nouns exist, output 'NONE'.
            """;

        String rawOutput = chatClient.prompt()
                .system(systemPrompt)
                .user("Extract PII from this text: " + input)
                .options(OllamaChatOptions.builder().temperature(0.7).build())
                .call()
                .content();

        if (rawOutput == null || rawOutput.contains("NONE") || rawOutput.isBlank()) {
            return Arrays.stream(rawOutput.split(","))
                    .map(String::trim) // Removes the leading spaces like " London"
                    .filter(s -> s.length() > 2) // Ignore accidental single characters
                    .distinct() // Removes duplicates like "example.com" if already in the email
                    .collect(Collectors.toList());
        }

        return Arrays.asList(rawOutput.split(","));
    }

    private String redactLogic(String text, List<String> piiList) {
        String temp = text;
        // Sort by length longest-first to prevent partial redaction (e.g., "William" vs "Will")
        List<String> sorted = piiList.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();

        for (String pii : sorted) {
            String regex = "(?i)\\b" + Pattern.quote(pii.trim()) + "\\b";
            temp = temp.replaceAll(regex, "[REDACTED]");
        }
        return temp;
    }
}