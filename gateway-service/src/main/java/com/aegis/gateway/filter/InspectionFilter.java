package com.aegis.gateway.filter;

import com.aegis.gateway.model.InspectionRequest;
import com.aegis.gateway.model.VerdictDecision;
import com.aegis.gateway.telemetry.TelemetryProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class InspectionFilter extends AbstractGatewayFilterFactory<InspectionFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(InspectionFilter.class);
    private final WebClient webClient;
    private final TelemetryProducer telemetryProducer;
    private final ObjectMapper objectMapper;
    private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilterFactory;

    public InspectionFilter(WebClient.Builder webClientBuilder, 
                            TelemetryProducer telemetryProducer, 
                            ObjectMapper objectMapper,
                            ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilterFactory) {
        super(Config.class);
        // Assuming inspection-service runs on localhost:8081 for MVP
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.telemetryProducer = telemetryProducer;
        this.objectMapper = objectMapper;
        this.modifyRequestBodyFilterFactory = modifyRequestBodyFilterFactory;
    }

    public static class Config {
        // Configuration properties can be added here
    }

    @Override
    public GatewayFilter apply(Config config) {
        // We use ModifyRequestBody to safely read and rewrite the body
        return modifyRequestBodyFilterFactory.apply(c -> c
                .setRewriteFunction(String.class, String.class, (exchange, originalBody) -> {
                    if (originalBody == null) {
                        return Mono.just("");
                    }

                    long startTime = System.currentTimeMillis();
                    // Basic extraction of prompt from OpenAI chat completion payload
                    String prompt = extractPromptFromOpenAiRequest(originalBody);
                    String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-Id");
                    if (tenantId == null) tenantId = "default-tenant";

                    InspectionRequest inspectionRequest = new InspectionRequest(tenantId, prompt);

                    String finalTenantId = tenantId;
                    return webClient.post()
                            .uri("/api/v1/inspect")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(inspectionRequest)
                            .retrieve()
                            .bodyToMono(VerdictDecision.class)
                            .flatMap(decision -> {
                                long latency = System.currentTimeMillis() - startTime;
                                
                                String matchedRules = decision.getMatchedRules() != null ? 
                                        String.join(";", decision.getMatchedRules()) : "";
                                
                                telemetryProducer.publishEvent(finalTenantId, decision.getVerdict(), matchedRules, latency);

                                if ("BLOCK".equalsIgnoreCase(decision.getVerdict())) {
                                    logger.warn("Request blocked for tenant: {}", finalTenantId);
                                    return Mono.error(new BlockedRequestException("Blocked by AEGIS Inspection"));
                                }
                                
                                return Mono.just(originalBody);
                            })
                            // If inspection fails, fail open for MVP (could be configurable)
                            .onErrorResume(e -> {
                                if (e instanceof BlockedRequestException) {
                                    return Mono.error(e); // Let it propagate to the error handler
                                }
                                logger.error("Inspection service failed, failing open", e);
                                return Mono.just(originalBody);
                            });
                })
        ).filter(
            // We chain another filter to catch the BlockedRequestException and return 403
            (exchange, chain) -> chain.filter(exchange).onErrorResume(BlockedRequestException.class, e -> {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            })
        );
    }

    private String extractPromptFromOpenAiRequest(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode messages = root.path("messages");
            if (messages.isArray() && messages.size() > 0) {
                // Return the content of the last message as the prompt to inspect
                return messages.get(messages.size() - 1).path("content").asText("");
            }
        } catch (JsonProcessingException e) {
            logger.debug("Failed to parse request body as JSON", e);
        }
        return body; // Fallback to raw body
    }

    public static class BlockedRequestException extends RuntimeException {
        public BlockedRequestException(String message) {
            super(message);
        }
    }
}
