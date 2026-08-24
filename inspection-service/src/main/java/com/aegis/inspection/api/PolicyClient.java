package com.aegis.inspection.api;

import com.aegis.inspection.model.PolicyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PolicyClient {

    private static final Logger logger = LoggerFactory.getLogger(PolicyClient.class);
    private final WebClient webClient;

    public PolicyClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://policy-service:8083").build();
    }

    @Cacheable(value = "tenantPolicies", key = "#tenantId", unless = "#result == null")
    public PolicyDto getPolicy(String tenantId) {
        logger.debug("Fetching policy for tenant: {} from policy-service", tenantId);
        try {
            return webClient.get()
                    .uri("/api/v1/policies/{tenantId}", tenantId)
                    .retrieve()
                    .bodyToMono(PolicyDto.class)
                    .block(); // Blocking is acceptable in this MVP since we use Web MVC instead of WebFlux for controllers
        } catch (Exception e) {
            logger.error("Failed to fetch policy for tenant: {}", tenantId, e);
            return null; // Fail open (or closed) based on logic
        }
    }
}
