package com.aegis.gateway.model;

public class InspectionRequest {
    private String tenantId;
    private String prompt;

    public InspectionRequest(String tenantId, String prompt) {
        this.tenantId = tenantId;
        this.prompt = prompt;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
