package com.example.expensetracker.service;

import com.example.expensetracker.dto.RuleMatchResult;
import com.example.expensetracker.model.RuleDefinition;
import com.example.expensetracker.repository.RuleDefinitionRepository;
import com.example.expensetracker.util.CategoryRuleEngine;
import com.example.expensetracker.util.DescriptionCleaner;
import com.example.expensetracker.util.RuleMatchScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CategorizationService {

    private static final Logger logger = LoggerFactory.getLogger(CategorizationService.class);

    private final CategoryRuleEngine categoryRuleEngine;
    private final RuleDefinitionRepository ruleRepository;

    @Autowired
    public CategorizationService(CategoryRuleEngine categoryRuleEngine,
                                 RuleDefinitionRepository ruleRepository) {
        this.categoryRuleEngine = categoryRuleEngine;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Categorize a transaction based on description and type using match score algorithm.
     * Prioritizes more specific matches over generic ones.
     */
    public String categorize(String rawDescription, String type) {
        if (rawDescription == null || rawDescription.isEmpty()) {
            return "Miscellaneous";
        }

        // Clean the description first
        String cleanedDescription = DescriptionCleaner.clean(rawDescription);

        // For CREDIT transactions, check if it's income-related
        if ("CREDIT".equalsIgnoreCase(type)) {
            String lowerDesc = cleanedDescription.toLowerCase();
            if (lowerDesc.contains("salary") ||
                lowerDesc.contains("credited") ||
                lowerDesc.contains("neft cr") ||
                lowerDesc.contains("imps cr") ||
                lowerDesc.contains("ach cr") ||
                lowerDesc.contains("rtgs cr") ||
                rawDescription.toLowerCase().contains("salary")) {
                return "Income";
            }
        }

        // Try to find the best matching rule using score-based matching
        String category = findBestMatchingCategory(cleanedDescription, type);

        // If no rule matched, use the legacy rule engine
        if (category.equals("Miscellaneous")) {
            category = categoryRuleEngine.findCategory(cleanedDescription);
        }

        // Also check against the original description for better matching
        if (category.equals("Miscellaneous")) {
            category = categoryRuleEngine.findCategory(rawDescription);
        }

        return category;
    }

    /**
     * Find the best matching category using match score algorithm.
     * Returns the category from the rule with the highest match score.
     */
    private String findBestMatchingCategory(String description, String type) {
        logger.info("╔════════════════════════════════════════════════════════════════╗");
        logger.info("║  FINDING BEST MATCHING CATEGORY                                ║");
        logger.info("╠════════════════════════════════════════════════════════════════╣");
        logger.info("║  Description: {}", description);
        logger.info("║  Type: {}", type);
        logger.info("╚════════════════════════════════════════════════════════════════╝");

        // Get all enabled rules
        List<RuleDefinition> allRules = ruleRepository.findByEnabledTrue();

        if (allRules.isEmpty()) {
            logger.warn("⚠️ No enabled rules found!");
            return "Miscellaneous";
        }

        logger.info("📋 Found {} enabled rules", allRules.size());

        // Calculate match scores for all rules
        List<RuleMatchResult> matches = new ArrayList<>();

        for (RuleDefinition rule : allRules) {
            // Check if rule applies to this transaction type
            if (!isRuleApplicableForType(rule, type)) {
                logger.debug("⏭️ Skipping rule '{}' - type mismatch (rule: {}, transaction: {})",
                    rule.getRuleName(), rule.getTransactionType(), type);
                continue;
            }

            logger.info("➡️ Evaluating rule '{}'...", rule.getRuleName());
            int score = RuleMatchScore.calculateMatchScore(description, rule);

            if (score > 0) {
                matches.add(new RuleMatchResult(rule, score, description));
                logger.info("   ✅ MATCH! Score: {}", score);
            } else {
                logger.info("   ❌ No match (score: 0)");
            }
        }

        // If no matches found, return Miscellaneous
        if (matches.isEmpty()) {
            logger.warn("❌ No rules matched the description!");
            return "Miscellaneous";
        }

        logger.info("\n📊 ════════ MATCH RESULTS SUMMARY ════════");
        for (RuleMatchResult match : matches) {
            logger.info("   • Rule: '{}' | Category: '{}' | Score: {}",
                match.getRule().getRuleName(),
                match.getRule().getCategoryName(),
                match.getScore());
        }

        // Sort by score (highest first), then by priority
        matches.sort(Comparator
                .comparingInt(RuleMatchResult::getScore)
                .reversed()
                .thenComparing((r1, r2) -> {
                    Integer p1 = r1.getRule().getPriority() != null ? r1.getRule().getPriority() : 0;
                    Integer p2 = r2.getRule().getPriority() != null ? r2.getRule().getPriority() : 0;
                    return p2.compareTo(p1);
                }));

        // Get the best match
        RuleMatchResult bestMatch = matches.get(0);
        logger.info("\n🏆 ════════ WINNER ════════");
        logger.info("   Rule: '{}'", bestMatch.getRule().getRuleName());
        logger.info("   Category: '{}'", bestMatch.getRule().getCategoryName());
        logger.info("   Pattern: '{}'", bestMatch.getRule().getPattern());
        logger.info("   Score: {}", bestMatch.getScore());
        logger.info("   Priority: {}", bestMatch.getRule().getPriority());
        logger.info("════════════════════════════════════\n");

        return bestMatch.getRule().getCategoryName();
    }

    /**
     * Check if a rule is applicable for a given transaction type
     */
    private boolean isRuleApplicableForType(RuleDefinition rule, String transactionType) {
        String ruleType = rule.getTransactionType();

        // If rule is for ANY type, it applies
        if (ruleType == null || "ANY".equalsIgnoreCase(ruleType)) {
            return true;
        }

        // Otherwise, check if types match
        return ruleType.equalsIgnoreCase(transactionType);
    }

    /**
     * Get the best matching rule with its score for a given description.
     * Useful for debugging and testing.
     */
    public Optional<RuleMatchResult> getBestMatchingRule(String description, String type) {
        String cleanedDescription = DescriptionCleaner.clean(description);
        List<RuleDefinition> allRules = ruleRepository.findByEnabledTrue();

        RuleMatchResult bestMatch = null;
        int highestScore = 0;

        for (RuleDefinition rule : allRules) {
            if (!isRuleApplicableForType(rule, type)) {
                continue;
            }

            int score = RuleMatchScore.calculateMatchScore(cleanedDescription, rule);
            if (score > highestScore) {
                highestScore = score;
                bestMatch = new RuleMatchResult(rule, score, cleanedDescription);
            }
        }

        return Optional.ofNullable(bestMatch);
    }
}


