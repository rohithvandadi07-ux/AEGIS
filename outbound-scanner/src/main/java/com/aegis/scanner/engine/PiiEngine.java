package com.aegis.scanner.engine;

import com.aegis.scanner.model.ScannerResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PiiEngine {

    // Simple patterns for MVP. In reality, these are much more robust.
    private static final List<Pattern> SECRET_PATTERNS = List.of(
            // Fake AWS key pattern
            Pattern.compile("(?i)AKIA[0-9A-Z]{16}"),
            // Basic SSN pattern
            Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"),
            // Mock credit card pattern (visa/mastercard simple start)
            Pattern.compile("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14})\\b")
    );

    public ScannerResponse scanAndRedact(String payload) {
        if (payload == null || payload.isBlank()) {
            return new ScannerResponse(payload, List.of(), false);
        }

        String redactedPayload = payload;
        List<String> detectedSecrets = new ArrayList<>();
        boolean isRedacted = false;

        for (Pattern pattern : SECRET_PATTERNS) {
            Matcher matcher = pattern.matcher(redactedPayload);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                isRedacted = true;
                detectedSecrets.add("MATCHED_PATTERN: " + pattern.pattern());
                // Replace with REDACTED
                matcher.appendReplacement(sb, "[REDACTED_SECRET]");
            }
            matcher.appendTail(sb);
            redactedPayload = sb.toString();
        }

        // Basic Entropy Check for high entropy strings (mock implementation)
        // High entropy strings (e.g., long random tokens) can be detected here

        return new ScannerResponse(redactedPayload, detectedSecrets, isRedacted);
    }
}
