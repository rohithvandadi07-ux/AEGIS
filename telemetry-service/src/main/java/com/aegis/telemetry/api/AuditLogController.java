package com.aegis.telemetry.api;

import com.aegis.telemetry.model.AuditLog;
import com.aegis.telemetry.repository.AuditLogRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}
