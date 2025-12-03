package com.example.expensetracker.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to normalize merchant / narration strings from bank statement descriptions.
 * Steps:
 * 1. Lowercase
 * 2. Replace multiple spaces with single space
 * 3. Remove noise tokens (config list)
 * 4. Extract merchant tokens for patterns (UPI, POS, AMZ*, Pay platforms)
 * 5. Remove standalone numbers & special chars (except letters, slash, space)
 * 6. Return the cleaned merchant text
 */
@Component
public class MerchantNormalizer {

    private static final Set<String> NOISE_WORDS = new HashSet<>(Arrays.asList(
            "upi","pos","amz","amazon","amazon marketplace","txn","transfer","neft","imps","cr","dr",
            "payment","from","phone","wdl","atm","ref","no","id","pay","paytm","gpay","phonepe"
    ));

    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern NON_ALLOWED = Pattern.compile("[^a-z/\\s]");
    private static final Pattern NUMBERS = Pattern.compile("\\b\\d+\\b");
    private static final Pattern UPI_PATTERN = Pattern.compile("upi[/-].*", Pattern.CASE_INSENSITIVE);
    private static final Pattern AMAZON_PATTERN = Pattern.compile("amz\\*?([a-z ]+)");
    private static final Pattern POS_PATTERN = Pattern.compile("^pos\\s+.*", Pattern.CASE_INSENSITIVE);

    /**
     * Normalize a raw description into a merchant-like string.
     */
    public String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        String text = raw.toLowerCase().trim();

        // Try specific extraction patterns first
        String extracted = extractUpiMerchant(text);
        if (extracted == null) {
            extracted = extractAmazonMerchant(text);
        }
        if (extracted == null) {
            extracted = extractPosMerchant(text);
        }
        if (extracted == null) {
            extracted = text; // fallback to full text
        }

        // Remove noise words
        extracted = removeNoiseWords(extracted);

        // Remove standalone numbers (except those inside upi id already handled)
        extracted = NUMBERS.matcher(extracted).replaceAll(" ");

        // Remove disallowed special characters (keep letters, slash, space)
        extracted = NON_ALLOWED.matcher(extracted).replaceAll(" ");

        // Condense spaces
        extracted = MULTI_SPACE.matcher(extracted).replaceAll(" ").trim();

        // If ended up empty try fallback simple token from original
        if (extracted.isBlank()) {
            extracted = firstMeaningfulToken(text);
        }

        return extracted;
    }

    private String firstMeaningfulToken(String text) {
        for (String part : text.split("\\s+")) {
            if (part.length() > 2 && part.chars().anyMatch(Character::isLetter)) {
                return part;
            }
        }
        return text;
    }

    private String removeNoiseWords(String input) {
        StringBuilder sb = new StringBuilder();
        for (String token : input.split("\\s+")) {
            if (!NOISE_WORDS.contains(token)) {
                sb.append(token).append(' ');
            }
        }
        return sb.toString().trim();
    }

    private String extractUpiMerchant(String text) {
        Matcher m = UPI_PATTERN.matcher(text);
        if (!m.find()) return null;
        // Split by / or - and find first non-numeric meaningful segment after initial upi/id tokens
        String segmentString = m.group();
        String[] parts = segmentString.split("[/-]");
        for (String p : parts) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.equals("upi") || trimmed.matches("\\d{6,}") || trimmed.equals("payment")) continue;
            // Remove underscores and digits inside merchant chunk
            String merchant = trimmed.replaceAll("_", " ").replaceAll("\\d", "").trim();
            if (merchant.length() > 2 && merchant.chars().anyMatch(Character::isLetter)) {
                return merchant;
            }
        }
        return null;
    }

    private String extractAmazonMerchant(String text) {
        Matcher m = AMAZON_PATTERN.matcher(text);
        if (m.find()) {
            String res = m.group(1).trim();
            if (res.startsWith("amazon")) return "amazon";
            return res;
        }
        if (text.contains("amazon")) return "amazon";
        return null;
    }

    private String extractPosMerchant(String text) {
        Matcher m = POS_PATTERN.matcher(text);
        if (!m.find()) return null;
        // Remove 'pos' and numbers, take first word with letters
        String withoutPos = text.replaceFirst("^pos\\s+", "");
        withoutPos = withoutPos.replaceAll("\\b\\d+\\b", " ");
        for (String token : withoutPos.split("\\s+")) {
            if (token.length() > 2 && token.chars().anyMatch(Character::isLetter) && !NOISE_WORDS.contains(token)) {
                return token;
            }
        }
        return null;
    }
}

