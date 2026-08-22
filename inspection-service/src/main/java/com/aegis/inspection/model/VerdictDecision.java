package com.aegis.inspection.model;

import java.util.List;

public class VerdictDecision {
    private Verdict verdict;
    private double confidenceScore;
    private List<String> matchedRules;

    public VerdictDecision() {}

    public VerdictDecision(Verdict verdict, double confidenceScore, List<String> matchedRules) {
        this.verdict = verdict;
        this.confidenceScore = confidenceScore;
        this.matchedRules = matchedRules;
    }

    public Verdict getVerdict() { return verdict; }
    public void setVerdict(Verdict verdict) { this.verdict = verdict; }
    
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public List<String> getMatchedRules() { return matchedRules; }
    public void setMatchedRules(List<String> matchedRules) { this.matchedRules = matchedRules; }
}
