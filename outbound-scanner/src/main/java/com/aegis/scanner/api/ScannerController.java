package com.aegis.scanner.api;

import com.aegis.scanner.engine.PiiEngine;
import com.aegis.scanner.model.ScannerRequest;
import com.aegis.scanner.model.ScannerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scan")
public class ScannerController {

    private final PiiEngine piiEngine;

    public ScannerController(PiiEngine piiEngine) {
        this.piiEngine = piiEngine;
    }

    @PostMapping
    public ResponseEntity<ScannerResponse> scanResponse(@RequestBody ScannerRequest request) {
        ScannerResponse response = piiEngine.scanAndRedact(request.getResponsePayload());
        return ResponseEntity.ok(response);
    }
}
