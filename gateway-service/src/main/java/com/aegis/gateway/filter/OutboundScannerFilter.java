package com.aegis.gateway.filter;

import com.aegis.gateway.model.ScannerRequest;
import com.aegis.gateway.model.ScannerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OutboundScannerFilter extends AbstractGatewayFilterFactory<OutboundScannerFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(OutboundScannerFilter.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory;

    public OutboundScannerFilter(WebClient.Builder webClientBuilder,
                                 ObjectMapper objectMapper,
                                 ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory) {
        super(Config.class);
        // Using Docker DNS hostname for MVP
        this.webClient = webClientBuilder.baseUrl("http://outbound-scanner:8082").build();
        this.objectMapper = objectMapper;
        this.modifyResponseBodyFilterFactory = modifyResponseBodyFilterFactory;
    }

    public static class Config {
        // Configuration properties can be added here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return modifyResponseBodyFilterFactory.apply(c -> c
                .setRewriteFunction(String.class, String.class, (exchange, originalBody) -> {
                    if (originalBody == null) {
                        return Mono.just("");
                    }

                    // Extract the text content from the OpenAI response
                    String responseText = extractTextFromOpenAiResponse(originalBody);
                    
                    if (responseText == null || responseText.isBlank()) {
                        return Mono.just(originalBody);
                    }

                    ScannerRequest request = new ScannerRequest(responseText);

                    return webClient.post()
                            .uri("/api/v1/scan")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(ScannerResponse.class)
                            .map(scannerResponse -> {
                                if (scannerResponse.isRedacted()) {
                                    logger.warn("PII Redacted from outbound response. Matched rules: {}", 
                                            scannerResponse.getDetectedSecrets());
                                    // Replace the text in the original JSON
                                    return replaceTextInOpenAiResponse(originalBody, scannerResponse.getRedactedPayload());
                                }
                                return originalBody;
                            })
                            // Fail open if scanner fails
                            .onErrorResume(e -> {
                                logger.error("Outbound scanner service failed, failing open", e);
                                return Mono.just(originalBody);
                            });
                })
        );
    }

    private String extractTextFromOpenAiResponse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                if (!message.isMissingNode()) {
                    return message.path("content").asText(null);
                }
            }
        } catch (JsonProcessingException e) {
            logger.debug("Failed to parse response body as JSON", e);
        }
        return null;
    }

    private String replaceTextInOpenAiResponse(String body, String redactedText) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                if (message.isObject()) {
                    ((ObjectNode) message).put("content", redactedText);
                    return objectMapper.writeValueAsString(root);
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to rewrite redacted JSON response", e);
        }
        return body; // Fallback to original body on error
    }
}
