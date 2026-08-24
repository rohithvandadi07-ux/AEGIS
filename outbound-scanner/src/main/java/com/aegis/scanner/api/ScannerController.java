package com.aegis.scanner.api;

import com.aegis.scanner.engine.PiiEngine;
import com.aegis.scanner.model.ScannerRequest;
import com.aegis.scanner.model.ScannerResponse;
import com.aegis.scanner.model.PolicyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scan")
public class ScannerController {

    private static final Logger logger = LoggerFactory.getLogger(ScannerController.class);
    private final PiiEngine piiEngine;
    private final PolicyClient policyClient;

    public ScannerController(PiiEngine piiEngine, PolicyClient policyClient) {
        this.piiEngine = piiEngine;
        this.policyClient = policyClient;
    }

    @PostMapping
    public ResponseEntity<ScannerResponse> scanResponse(@RequestBody ScannerRequest request) {
        String tenantId = request.getTenantId();
        
        if (tenantId != null) {
            PolicyDto policy = policyClient.getPolicy(tenantId);
            if (policy != null && (!policy.isEnabled() || (policy.getConfig() != null && policy.getConfig().contains("\"disablePii\": true")))) {
                logger.debug("PII redaction disabled for tenant: {}", tenantId);
                return ResponseEntity.ok(new ScannerResponse(request.getResponsePayload(), List.of("BYPASSED_BY_POLICY"), false));
            }
        }

        ScannerResponse response = piiEngine.scanAndRedact(request.getResponsePayload());
        return ResponseEntity.ok(response);
    }
}
