package com.aegis.gateway.model;

public class ScannerRequest {
    private String responsePayload;

    public ScannerRequest() {}

    public ScannerRequest(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }
}
