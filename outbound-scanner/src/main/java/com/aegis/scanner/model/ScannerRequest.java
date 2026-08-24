package com.aegis.scanner.model;

public class ScannerRequest {
    private String tenantId;
    private String responsePayload;

    public ScannerRequest() {}

    public ScannerRequest(String tenantId, String responsePayload) {
        this.tenantId = tenantId;
        this.responsePayload = responsePayload;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }
}
