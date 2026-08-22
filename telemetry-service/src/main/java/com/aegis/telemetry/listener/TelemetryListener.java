package com.aegis.telemetry.listener;

import com.aegis.telemetry.model.AuditLog;
import com.aegis.telemetry.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TelemetryListener {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryListener.class);
    private final AuditLogRepository auditLogRepository;

    public TelemetryListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = "aegis.telemetry", groupId = "aegis-telemetry-group")
    public void listenTelemetryEvents(String message) {
        logger.info("Received telemetry event: {}", message);
        // For MVP, we'll assume the message is a simple comma-separated string:
        // tenantId,verdict,matchedRules,latencyMs
        try {
            String[] parts = message.split(",", 4);
            if (parts.length == 4) {
                AuditLog log = new AuditLog(parts[0], parts[1], parts[2], Long.parseLong(parts[3]));
                auditLogRepository.save(log);
            }
        } catch (Exception e) {
            logger.error("Failed to parse telemetry event", e);
        }
    }
}
