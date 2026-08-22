package com.aegis.gateway.model;

import java.util.List;

public class VerdictDecision {
    private String verdict;
    private double confidenceScore;
    private List<String> matchedRules;

    public VerdictDecision() {}

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public List<String> getMatchedRules() { return matchedRules; }
    public void setMatchedRules(List<String> matchedRules) { this.matchedRules = matchedRules; }
}
