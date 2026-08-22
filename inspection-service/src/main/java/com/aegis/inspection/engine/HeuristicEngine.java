package com.aegis.inspection.engine;

import com.aegis.inspection.model.Verdict;
import com.aegis.inspection.model.VerdictDecision;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class HeuristicEngine {

    // Common jailbreak phrases
    private static final List<Pattern> JAILBREAK_PATTERNS = List.of(
            Pattern.compile("ignore previous instructions", Pattern.CASE_INSENSITIVE),
            Pattern.compile("you are now a", Pattern.CASE_INSENSITIVE),
            Pattern.compile("DAN", Pattern.CASE_INSENSITIVE), // Do Anything Now
            Pattern.compile("system prompt", Pattern.CASE_INSENSITIVE)
    );

    public VerdictDecision analyze(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return new VerdictDecision(Verdict.ALLOW, 1.0, List.of());
        }

        List<String> matchedRules = new ArrayList<>();
        boolean isBlocked = false;

        // Check against known jailbreak patterns
        for (Pattern pattern : JAILBREAK_PATTERNS) {
            if (pattern.matcher(prompt).find()) {
                matchedRules.add("HEURISTIC_JAILBREAK_PATTERN_MATCH: " + pattern.pattern());
                isBlocked = true;
            }
        }

        // Basic check for encoding smuggling (e.g., base64 string that might decode to a prompt)
        // A very simple heuristic: if there's a long continuous alphanumeric string with == at the end
        if (prompt.matches(".*[A-Za-z0-9+/]{20,}={0,2}.*")) {
             // In a real implementation we would decode and re-evaluate
             matchedRules.add("HEURISTIC_ENCODING_SUSPICION");
             // For now we might just flag it
             if (!isBlocked) {
                 return new VerdictDecision(Verdict.FLAG_FOR_REVIEW, 0.5, matchedRules);
             }
        }

        if (isBlocked) {
            return new VerdictDecision(Verdict.BLOCK, 0.95, matchedRules);
        }

        return new VerdictDecision(Verdict.ALLOW, 0.9, List.of());
    }
}
