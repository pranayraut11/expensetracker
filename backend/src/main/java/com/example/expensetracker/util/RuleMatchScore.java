package com.example.expensetracker.util;

import com.example.expensetracker.model.RuleDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for calculating match scores for categorization rules.
 * Higher scores indicate more specific matches.
 */
public class RuleMatchScore {

    private static final Logger logger = LoggerFactory.getLogger(RuleMatchScore.class);

    /**
     * Calculate match score for a rule against a description.
     * Returns 0 if no match, higher scores for more specific matches.
     *
     * Scoring algorithm:
     * - Exact phrase match: +100 points per word in pattern
     * - Partial word match: +10 points per matching word
     * - Pattern length bonus: +1 point per character (more specific patterns score higher)
     * - Position bonus: +50 points if match is at the beginning
     * - Full word boundary match: +20 points per word
     *
     * @param description The transaction description to match against
     * @param rule The rule to evaluate
     * @return Match score (0 = no match, higher = more specific match)
     */
    public static int calculateMatchScore(String description, RuleDefinition rule) {
        if (description == null || description.isEmpty() || rule == null || rule.getPattern() == null) {
            return 0;
        }

        String pattern = rule.getPattern();
        String lowerDesc = description.toLowerCase();
        String lowerPattern = pattern.toLowerCase();

        logger.info("════════════════════════════════════════════════════════");
        logger.info("📊 CALCULATING MATCH SCORE");
        logger.info("Rule Name: {}", rule.getRuleName());
        logger.info("Category: {}", rule.getCategoryName());
        logger.info("Pattern: {}", pattern);
        logger.info("Description: {}", description);
        logger.info("════════════════════════════════════════════════════════");

        // Check if pattern is an alternation (OR pattern with |)
        if (isAlternationPattern(pattern)) {
            logger.info("✅ Detected ALTERNATION pattern (contains |)");
            int score = calculateAlternationScore(lowerDesc, pattern);
            logger.info("🎯 FINAL SCORE for rule '{}': {}", rule.getRuleName(), score);
            logger.info("════════════════════════════════════════════════════════\n");
            return score;
        }

        // Check if pattern is a regex (contains special regex characters)
        boolean isRegex = pattern.contains(".*") || pattern.contains("\\w") ||
                         pattern.contains("^") || pattern.contains("$") ||
                         pattern.contains("[") || pattern.contains("]");

        int score = 0;

        if (isRegex) {
            logger.info("✅ Detected REGEX pattern");
            // Handle regex patterns
            try {
                Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = regexPattern.matcher(description);
                if (matcher.find()) {
                    score = calculateRegexScore(matcher, pattern);
                    logger.info("✅ Regex matched! Score: {}", score);
                } else {
                    logger.info("❌ Regex did not match");
                }
            } catch (Exception e) {
                logger.warn("⚠️ Regex compilation failed, treating as plain text: {}", e.getMessage());
                // If regex fails, treat as plain text
                score = calculatePlainTextScore(lowerDesc, lowerPattern);
            }
        } else {
            logger.info("✅ Treating as PLAIN TEXT pattern");
            // Handle plain text patterns
            score = calculatePlainTextScore(lowerDesc, lowerPattern);
        }

        logger.info("🎯 FINAL SCORE for rule '{}': {}", rule.getRuleName(), score);
        logger.info("════════════════════════════════════════════════════════\n");
        return score;
    }

    /**
     * Check if pattern is an alternation pattern (OR with |)
     * Examples: (CAR|BIKE), (EMI|car loan repayment|home loan)
     */
    private static boolean isAlternationPattern(String pattern) {
        // Pattern starts with ( and ends with ) and contains |
        return pattern.matches("^\\([^()]*\\|[^()]*\\)$");
    }

    /**
     * Calculate score for alternation patterns (OR with |)
     * Returns the highest score among all alternatives
     */
    private static int calculateAlternationScore(String description, String pattern) {
        logger.info("🔍 Processing ALTERNATION pattern");

        // Remove surrounding parentheses
        String content = pattern.substring(1, pattern.length() - 1);
        logger.info("   Pattern content (without parens): {}", content);

        // Split by | to get alternatives
        String[] alternatives = content.split("\\|");
        logger.info("   Number of alternatives: {}", alternatives.length);

        int maxScore = 0;
        String bestMatch = null;

        for (int i = 0; i < alternatives.length; i++) {
            String trimmedAlt = alternatives[i].trim();
            if (trimmedAlt.isEmpty()) {
                continue;
            }

            logger.info("   ┌─ Alternative #{}: '{}'", i + 1, trimmedAlt);

            // Calculate score for this alternative as plain text
            int altScore = calculatePlainTextScore(description.toLowerCase(), trimmedAlt.toLowerCase());

            logger.info("   │  Score: {}", altScore);

            if (altScore > maxScore) {
                maxScore = altScore;
                bestMatch = trimmedAlt;
                logger.info("   └─ ✅ NEW BEST MATCH! Score: {}", maxScore);
            } else {
                logger.info("   └─");
            }
        }

        logger.info("🏆 Best matching alternative: '{}' with score: {}", bestMatch, maxScore);
        return maxScore;
    }

    /**
     * Calculate score for plain text pattern matching
     */
    private static int calculatePlainTextScore(String description, String pattern) {
        logger.debug("      🔎 Checking plain text match for pattern: '{}'", pattern);

        if (!description.contains(pattern)) {
            logger.debug("      ❌ Exact phrase not found, checking partial word matches...");
            // Check for partial word matches
            return calculatePartialWordScore(description, pattern);
        }

        logger.debug("      ✅ Exact phrase FOUND in description!");

        int score = 0;

        // Base score: pattern length (longer patterns are more specific)
        int lengthScore = pattern.length();
        score += lengthScore;
        logger.debug("         + Length bonus: {} (pattern length)", lengthScore);

        // Count words in pattern (more words = more specific)
        String[] patternWords = pattern.trim().split("\\s+");
        int wordScore = patternWords.length * 100;
        score += wordScore;
        logger.debug("         + Word count bonus: {} ({} words × 100)", wordScore, patternWords.length);

        // Check if it's at the beginning of description (high confidence)
        if (description.startsWith(pattern)) {
            score += 50;
            logger.debug("         + Position bonus: 50 (match at start)");
        }

        // Check if pattern is surrounded by word boundaries (exact word match)
        String boundaryPattern = "\\b" + Pattern.quote(pattern) + "\\b";
        if (description.matches(".*" + boundaryPattern + ".*")) {
            int boundaryScore = patternWords.length * 20;
            score += boundaryScore;
            logger.debug("         + Word boundary bonus: {} ({} words × 20)", boundaryScore, patternWords.length);
        }

        logger.debug("      📊 Total score: {}", score);
        return score;
    }

    /**
     * Calculate score for partial word matches
     */
    private static int calculatePartialWordScore(String description, String pattern) {
        logger.debug("      🔍 Calculating PARTIAL word match score");

        String[] patternWords = pattern.trim().split("\\s+");
        String[] descWords = description.trim().split("\\s+");

        int matchingWords = 0;
        int exactWordMatches = 0;

        logger.debug("         Pattern words: {} | Description words: {}", patternWords.length, descWords.length);

        for (String patternWord : patternWords) {
            if (patternWord.isEmpty()) continue;

            for (String descWord : descWords) {
                if (descWord.equals(patternWord)) {
                    exactWordMatches++;
                    matchingWords++;
                    logger.debug("         ✅ Exact word match: '{}'", patternWord);
                    break;
                } else if (descWord.contains(patternWord) || patternWord.contains(descWord)) {
                    matchingWords++;
                    logger.debug("         ✓ Partial word match: '{}' in '{}'", patternWord, descWord);
                    break;
                }
            }
        }

        // If no words match, return 0
        if (matchingWords == 0) {
            logger.debug("      ❌ No matching words found - Score: 0");
            return 0;
        }

        // Calculate score based on matching words
        int score = matchingWords * 10;
        logger.debug("         + Matching words: {} × 10 = {}", matchingWords, matchingWords * 10);

        int exactBonus = exactWordMatches * 20;
        score += exactBonus;
        logger.debug("         + Exact matches: {} × 20 = {}", exactWordMatches, exactBonus);

        // Bonus for matching all pattern words
        if (matchingWords == patternWords.length) {
            score += 50;
            logger.debug("         + All words matched bonus: 50");
        }

        logger.debug("      📊 Partial match total score: {}", score);
        return score;
    }

    /**
     * Calculate score for regex pattern matching
     */
    private static int calculateRegexScore(Matcher matcher, String pattern) {
        int score = 0;

        // Base score for regex match
        score += 50;

        // Get the matched text
        String matchedText = matcher.group();
        if (matchedText != null && !matchedText.isEmpty()) {
            // Score based on length of matched text (longer = more specific)
            score += matchedText.length();

            // Count words in matched text
            String[] matchedWords = matchedText.trim().split("\\s+");
            score += matchedWords.length * 30;
        }

        // Score based on pattern complexity (more complex = more specific)
        score += countRegexComplexity(pattern);

        return score;
    }

    /**
     * Count regex pattern complexity
     */
    private static int countRegexComplexity(String pattern) {
        int complexity = 0;

        // Count special regex constructs
        if (pattern.contains("^")) complexity += 10; // Start anchor
        if (pattern.contains("$")) complexity += 10; // End anchor
        if (pattern.contains("\\b")) complexity += 10; // Word boundary

        // Count character classes
        complexity += (pattern.length() - pattern.replace("[", "").length()) * 5;

        // Count alternations (|)
        complexity += (pattern.length() - pattern.replace("|", "").length()) * 5;

        return complexity;
    }

    /**
     * Compare two rules based on their match scores against a description.
     * Returns negative if rule1 should come before rule2 (higher priority).
     *
     * @param description The description to match against
     * @param rule1 First rule
     * @param rule2 Second rule
     * @return Negative if rule1 has higher priority, positive if rule2 has higher priority
     */
    public static int compareRules(String description, RuleDefinition rule1, RuleDefinition rule2) {
        int score1 = calculateMatchScore(description, rule1);
        int score2 = calculateMatchScore(description, rule2);

        // Higher score should come first (so reverse the comparison)
        int scoreComparison = Integer.compare(score2, score1);

        if (scoreComparison != 0) {
            return scoreComparison;
        }

        // If scores are equal, use the rule's priority
        Integer priority1 = rule1.getPriority() != null ? rule1.getPriority() : 0;
        Integer priority2 = rule2.getPriority() != null ? rule2.getPriority() : 0;

        return Integer.compare(priority2, priority1);
    }
}
