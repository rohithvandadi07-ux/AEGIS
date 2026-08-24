package com.aegis.inspection.model;

public class PolicyDto {
    private String tenantId;
    private String config;
    private boolean enabled;

    public PolicyDto() {}

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
