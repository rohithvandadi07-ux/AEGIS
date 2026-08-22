package com.aegis.inspection.api;

import com.aegis.inspection.engine.HeuristicEngine;
import com.aegis.inspection.model.InspectionRequest;
import com.aegis.inspection.model.VerdictDecision;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspect")
public class InspectionController {

    private final HeuristicEngine heuristicEngine;

    public InspectionController(HeuristicEngine heuristicEngine) {
        this.heuristicEngine = heuristicEngine;
    }

    @PostMapping
    public ResponseEntity<VerdictDecision> inspectPrompt(@RequestBody InspectionRequest request) {
        VerdictDecision decision = heuristicEngine.analyze(request.getPrompt());
        return ResponseEntity.ok(decision);
    }
}
