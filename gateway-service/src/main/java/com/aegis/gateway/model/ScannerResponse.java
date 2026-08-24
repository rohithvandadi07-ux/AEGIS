package com.aegis.gateway.model;

import java.util.List;

public class ScannerResponse {
    private String redactedPayload;
    private List<String> detectedSecrets;
    private boolean isRedacted;

    public ScannerResponse() {}

    public ScannerResponse(String redactedPayload, List<String> detectedSecrets, boolean isRedacted) {
        this.redactedPayload = redactedPayload;
        this.detectedSecrets = detectedSecrets;
        this.isRedacted = isRedacted;
    }

    public String getRedactedPayload() { return redactedPayload; }
    public void setRedactedPayload(String redactedPayload) { this.redactedPayload = redactedPayload; }

    public List<String> getDetectedSecrets() { return detectedSecrets; }
    public void setDetectedSecrets(List<String> detectedSecrets) { this.detectedSecrets = detectedSecrets; }

    public boolean isRedacted() { return isRedacted; }
    public void setRedacted(boolean redacted) { this.isRedacted = redacted; }
}
