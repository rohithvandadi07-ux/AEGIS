package com.aegis.inspection.api;

import com.aegis.inspection.engine.HeuristicEngine;
import com.aegis.inspection.model.InspectionRequest;
import com.aegis.inspection.model.VerdictDecision;
import com.aegis.inspection.model.Verdict;
import com.aegis.inspection.model.PolicyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inspect")
public class InspectionController {

    private static final Logger logger = LoggerFactory.getLogger(InspectionController.class);
    private final HeuristicEngine heuristicEngine;
    private final PolicyClient policyClient;

    public InspectionController(HeuristicEngine heuristicEngine, PolicyClient policyClient) {
        this.heuristicEngine = heuristicEngine;
        this.policyClient = policyClient;
    }

    @PostMapping
    public ResponseEntity<VerdictDecision> inspectPrompt(@RequestBody InspectionRequest request) {
        String tenantId = request.getTenantId();
        
        if (tenantId != null) {
            PolicyDto policy = policyClient.getPolicy(tenantId);
            if (policy != null && (!policy.isEnabled() || (policy.getConfig() != null && policy.getConfig().contains("\"disableHeuristics\": true")))) {
                logger.debug("Heuristics disabled for tenant: {}", tenantId);
                return ResponseEntity.ok(new VerdictDecision(Verdict.ALLOW, 1.0, List.of("BYPASSED_BY_POLICY")));
            }
        }

        VerdictDecision decision = heuristicEngine.analyze(request.getPrompt());
        return ResponseEntity.ok(decision);
    }
}
