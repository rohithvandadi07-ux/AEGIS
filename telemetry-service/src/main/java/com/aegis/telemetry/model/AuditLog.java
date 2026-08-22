package com.aegis.telemetry.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String matchedRules;

    private long latencyMs;

    private Instant timestamp;

    public AuditLog() {
        this.timestamp = Instant.now();
    }

    public AuditLog(String tenantId, String verdict, String matchedRules, long latencyMs) {
        this.tenantId = tenantId;
        this.verdict = verdict;
        this.matchedRules = matchedRules;
        this.latencyMs = latencyMs;
        this.timestamp = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getMatchedRules() { return matchedRules; }
    public void setMatchedRules(String matchedRules) { this.matchedRules = matchedRules; }

    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
