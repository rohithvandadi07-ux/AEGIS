package com.aegis.inspection.engine;

import com.aegis.inspection.model.ThreatSignature;
import com.aegis.inspection.model.Verdict;
import com.aegis.inspection.model.VerdictDecision;
import com.aegis.inspection.repository.ThreatSignatureRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.pgvector.PGvector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SemanticEngine {

    private static final Logger logger = LoggerFactory.getLogger(SemanticEngine.class);
    private static final double SIMILARITY_THRESHOLD = 0.15; // Represents distance < 0.15 (so similarity > 0.85)
    
    private final ThreatSignatureRepository repository;
    private final WebClient webClient;

    public SemanticEngine(ThreatSignatureRepository repository, 
                          WebClient.Builder webClientBuilder,
                          @Value("${openai.api.key}") String apiKey) {
        this.repository = repository;
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    public VerdictDecision analyze(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return new VerdictDecision(Verdict.ALLOW, 1.0, List.of());
        }

        try {
            float[] embedding = fetchEmbedding(prompt);
            if (embedding == null) {
                logger.warn("Failed to generate embedding for prompt, bypassing semantic check");
                return new VerdictDecision(Verdict.ALLOW, 1.0, List.of("SEMANTIC_BYPASSED"));
            }

            PGvector pgVector = new PGvector(embedding);
            
            // Query vector database for nearest neighbors
            List<ThreatSignature> similarThreats = repository.findSimilarThreats(pgVector.getValue(), SIMILARITY_THRESHOLD, 1);
            
            if (!similarThreats.isEmpty()) {
                ThreatSignature match = similarThreats.get(0);
                logger.warn("Semantic threat detected! Matched signature: {}", match.getSignatureName());
                return new VerdictDecision(Verdict.BLOCK, 0.0, List.of("SEMANTIC_THREAT_" + match.getSignatureName()));
            }

        } catch (Exception e) {
            logger.error("Error during semantic analysis", e);
            // Fail open on error
            return new VerdictDecision(Verdict.ALLOW, 1.0, List.of("SEMANTIC_ERROR"));
        }

        return new VerdictDecision(Verdict.ALLOW, 1.0, List.of());
    }

    public float[] fetchEmbedding(String text) {
        try {
            Map<String, Object> request = Map.of(
                    "input", text,
                    "model", "text-embedding-ada-002"
            );

            JsonNode response = webClient.post()
                    .uri("/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null && response.has("data") && response.get("data").isArray()) {
                JsonNode vectorNode = response.get("data").get(0).get("embedding");
                if (vectorNode.isArray()) {
                    float[] vector = new float[vectorNode.size()];
                    for (int i = 0; i < vectorNode.size(); i++) {
                        vector[i] = (float) vectorNode.get(i).asDouble();
                    }
                    return vector;
                }
            }
        } catch (Exception e) {
            logger.error("OpenAI embedding API failed", e);
        }
        return null;
    }
}
