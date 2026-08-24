package com.aegis.policy.api;

import com.aegis.policy.model.Policy;
import com.aegis.policy.repository.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@CrossOrigin(origins = "*")
public class PolicyController {

    private final PolicyRepository policyRepository;

    public PolicyController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<Policy> getPolicyByTenant(@PathVariable String tenantId) {
        return policyRepository.findByTenantId(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Policy createOrUpdatePolicy(@RequestBody Policy policy) {
        return policyRepository.findByTenantId(policy.getTenantId())
                .map(existing -> {
                    existing.setConfig(policy.getConfig());
                    existing.setEnabled(policy.isEnabled());
                    return policyRepository.save(existing);
                })
                .orElseGet(() -> policyRepository.save(policy));
    }
}
