package com.aegis.gateway.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TelemetryProducer {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryProducer.class);
    private static final String TOPIC = "aegis.telemetry";
    
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TelemetryProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String tenantId, String verdict, String matchedRules, long latencyMs) {
        // Simple CSV format for MVP: tenantId,verdict,matchedRules,latencyMs
        String message = String.format("%s,%s,%s,%d", tenantId, verdict, matchedRules, latencyMs);
        logger.debug("Publishing telemetry event: {}", message);
        kafkaTemplate.send(TOPIC, tenantId, message);
    }
}
