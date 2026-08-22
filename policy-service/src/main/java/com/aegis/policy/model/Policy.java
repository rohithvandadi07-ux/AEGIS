package com.aegis.policy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenantId;

    // e.g., JSON string or YAML string representing the policy configuration
    @Column(columnDefinition = "TEXT")
    private String config;

    private boolean enabled = true;

    public Policy() {}

    public Policy(String tenantId, String config, boolean enabled) {
        this.tenantId = tenantId;
        this.config = config;
        this.enabled = enabled;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
