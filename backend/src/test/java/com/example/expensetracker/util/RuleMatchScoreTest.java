package com.example.expensetracker.util;

import com.example.expensetracker.model.RuleDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleMatchScoreTest {

    @Test
    void testExactPhraseMatch() {
        RuleDefinition rule = createRule("Car Loan Repayment", "car loan repayment");
        int score = RuleMatchScore.calculateMatchScore("car loan repayment", rule);

        // Should have high score for exact match
        assertTrue(score > 300, "Exact match should have score > 300, got: " + score);
    }

    @Test
    void testPartialMatch() {
        RuleDefinition rule = createRule("Car Loan", "car loan");
        int score = RuleMatchScore.calculateMatchScore("car loan repayment to bank", rule);

        // Should match but with lower score than exact match
        assertTrue(score > 200, "Partial match should have score > 200, got: " + score);
    }

    @Test
    void testMostSpecificWins() {
        String description = "car loan repayment to HDFC bank";

        RuleDefinition genericRule = createRule("Car", "car");
        RuleDefinition mediumRule = createRule("Car Loan", "car loan");
        RuleDefinition specificRule = createRule("Car Loan Repayment", "car loan repayment");

        int genericScore = RuleMatchScore.calculateMatchScore(description, genericRule);
        int mediumScore = RuleMatchScore.calculateMatchScore(description, mediumRule);
        int specificScore = RuleMatchScore.calculateMatchScore(description, specificRule);

        assertTrue(specificScore > mediumScore,
            "Specific rule should score higher: " + specificScore + " vs " + mediumScore);
        assertTrue(mediumScore > genericScore,
            "Medium rule should score higher: " + mediumScore + " vs " + genericScore);

        System.out.println("Scores - Generic: " + genericScore +
                         ", Medium: " + mediumScore +
                         ", Specific: " + specificScore);
    }

    @Test
    void testNoMatch() {
        RuleDefinition rule = createRule("Shopping", "amazon");
        int score = RuleMatchScore.calculateMatchScore("car loan repayment", rule);

        assertEquals(0, score, "No match should return score 0");
    }

    @Test
    void testCaseInsensitive() {
        RuleDefinition rule = createRule("Car Loan", "car loan");

        int score1 = RuleMatchScore.calculateMatchScore("CAR LOAN repayment", rule);
        int score2 = RuleMatchScore.calculateMatchScore("car loan repayment", rule);

        assertTrue(score1 > 0, "Should match regardless of case");
        assertTrue(score2 > 0, "Should match regardless of case");
    }

    @Test
    void testPositionBonus() {
        RuleDefinition rule = createRule("HDFC", "hdfc");

        int scoreAtStart = RuleMatchScore.calculateMatchScore("hdfc car loan", rule);
        int scoreInMiddle = RuleMatchScore.calculateMatchScore("car loan hdfc", rule);

        assertTrue(scoreAtStart > scoreInMiddle,
            "Match at start should score higher: " + scoreAtStart + " vs " + scoreInMiddle);
    }

    @Test
    void testWordBoundaries() {
        RuleDefinition rule = createRule("Car", "car");

        String exactWordMatch = "car loan";  // "car" is a complete word
        String partialWordMatch = "scar tissue";  // "car" is part of "scar"

        int exactScore = RuleMatchScore.calculateMatchScore(exactWordMatch, rule);
        int partialScore = RuleMatchScore.calculateMatchScore(partialWordMatch, rule);

        assertTrue(exactScore > partialScore,
            "Exact word boundary should score higher: " + exactScore + " vs " + partialScore);
    }

    @Test
    void testRuleComparison() {
        String description = "car loan repayment";

        RuleDefinition rule1 = createRule("Specific", "car loan repayment");
        RuleDefinition rule2 = createRule("Generic", "car");

        int comparison = RuleMatchScore.compareRules(description, rule1, rule2);

        assertTrue(comparison < 0, "More specific rule should come first (negative comparison)");
    }

    @Test
    void testPriorityTiebreaker() {
        String description = "car loan";

        RuleDefinition highPriority = createRule("High Priority", "car loan");
        highPriority.setPriority(100);

        RuleDefinition lowPriority = createRule("Low Priority", "car loan");
        lowPriority.setPriority(10);

        int comparison = RuleMatchScore.compareRules(description, highPriority, lowPriority);

        assertTrue(comparison < 0, "Higher priority should come first when scores are equal");
    }

    @Test
    void testNullHandling() {
        RuleDefinition rule = createRule("Test", "test");

        assertEquals(0, RuleMatchScore.calculateMatchScore(null, rule));
        assertEquals(0, RuleMatchScore.calculateMatchScore("", rule));
        assertEquals(0, RuleMatchScore.calculateMatchScore("test", null));
    }

    @Test
    void testMultiWordPattern() {
        RuleDefinition rule = createRule("Specific Rule", "mutual fund sip investment");

        int fullMatch = RuleMatchScore.calculateMatchScore("mutual fund sip investment in HDFC", rule);
        int partialMatch = RuleMatchScore.calculateMatchScore("mutual fund investment", rule);
        int singleMatch = RuleMatchScore.calculateMatchScore("investment in stocks", rule);

        assertTrue(fullMatch > partialMatch, "All words matching should score highest");
        assertTrue(partialMatch > singleMatch, "More words matching should score higher");
        assertTrue(singleMatch > 0, "Single word match should still score points");
    }

    @Test
    void testAlternationPattern() {
        // Test case from user's issue
        String description = "IMPS-605727581167-PRANAY RAUT HOME LOAN-SBIN-XXXXXXX5139-CAR LOAN REPAYMENT";

        RuleDefinition loansRule = createRule("Loans Rule", "(EMI|PRANAY RAUT HOME LOAN|car emi|LOAN-SBIN|car loan repayment)");
        RuleDefinition vehicleRule = createRule("Vehicle Rule", "(BIKE|CAR|wheel alignment)");

        int loansScore = RuleMatchScore.calculateMatchScore(description, loansRule);
        int vehicleScore = RuleMatchScore.calculateMatchScore(description, vehicleRule);

        System.out.println("Loans Rule Score: " + loansScore);
        System.out.println("Vehicle Rule Score: " + vehicleScore);

        assertTrue(loansScore > 0, "Loans rule should match");
        assertTrue(vehicleScore > 0, "Vehicle rule should match");
        assertTrue(loansScore > vehicleScore,
            "Loans rule should score higher than vehicle rule: " + loansScore + " vs " + vehicleScore);
    }

    @Test
    void testAlternationWithSpecificMatch() {
        RuleDefinition rule = createRule("Test", "(shopping|amazon|flipkart)");

        int genericMatch = RuleMatchScore.calculateMatchScore("I like shopping", rule);
        int specificMatch = RuleMatchScore.calculateMatchScore("amazon prime subscription", rule);

        assertTrue(genericMatch > 0, "Should match 'shopping'");
        assertTrue(specificMatch > 0, "Should match 'amazon'");
    }

    @Test
    void testAlternationBestMatchWins() {
        // Pattern has both short and long alternatives
        RuleDefinition rule = createRule("Test", "(car|car loan|car loan repayment)");
        String description = "car loan repayment to bank";

        int score = RuleMatchScore.calculateMatchScore(description, rule);

        // Should match the most specific alternative (car loan repayment)
        // which should give a higher score than just matching "car"
        assertTrue(score > 300, "Should use most specific alternative: " + score);
    }

    // Helper method to create test rules
    private RuleDefinition createRule(String name, String pattern) {
        RuleDefinition rule = new RuleDefinition();
        rule.setRuleName(name);
        rule.setPattern(pattern);
        rule.setCategoryName("Test Category");
        rule.setPriority(0);
        rule.setEnabled(true);
        return rule;
    }
}
