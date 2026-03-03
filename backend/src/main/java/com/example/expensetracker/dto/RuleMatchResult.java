package com.example.expensetracker.dto;

import com.example.expensetracker.model.RuleDefinition;

/**
 * DTO to hold a rule match result with its score
 */
public class RuleMatchResult {
    private RuleDefinition rule;
    private int score;
    private String matchedText;

    public RuleMatchResult(RuleDefinition rule, int score) {
        this.rule = rule;
        this.score = score;
    }

    public RuleMatchResult(RuleDefinition rule, int score, String matchedText) {
        this.rule = rule;
        this.score = score;
        this.matchedText = matchedText;
    }

    public RuleDefinition getRule() {
        return rule;
    }

    public void setRule(RuleDefinition rule) {
        this.rule = rule;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }

    @Override
    public String toString() {
        return "RuleMatchResult{" +
                "ruleName=" + (rule != null ? rule.getRuleName() : "null") +
                ", category=" + (rule != null ? rule.getCategoryName() : "null") +
                ", score=" + score +
                ", matchedText='" + matchedText + '\'' +
                '}';
    }
}
