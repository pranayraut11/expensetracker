package com.example.expensetracker.util;


import java.util.regex.Pattern;


public class DescriptionCleaner {

    private static final Pattern NUMBERS_PATTERN = Pattern.compile("\\d+");
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[^a-z\\s]");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s+");

    /**
     * Clean and normalize transaction description
     */
    public static String clean(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }

        // Convert to lowercase
        String cleaned = description.toLowerCase();

        // Extract merchant name from UPI strings
        // Example: "UPI/1234/Swiggy_BLR" -> "swiggy"
        if (cleaned.contains("upi/") || cleaned.contains("upi-")) {
            cleaned = extractUpiMerchant(cleaned);
        }

        // Extract merchant from POS transactions
        // Example: "POS 402912 SWIGGY BLR IN" -> "swiggy"
        if (cleaned.startsWith("pos ")) {
            cleaned = extractPosMerchant(cleaned);
        }

        // Extract merchant from Amazon transactions
        // Example: "AMZ*Amazon Marketplace" -> "amazon"
        if (cleaned.startsWith("amz") || cleaned.contains("amazon")) {
            cleaned = "amazon";
        }

        // Remove numbers
        cleaned = NUMBERS_PATTERN.matcher(cleaned).replaceAll(" ");

        // Remove special characters
        cleaned = SPECIAL_CHARS_PATTERN.matcher(cleaned).replaceAll(" ");

        // Replace multiple spaces with single space
        cleaned = MULTIPLE_SPACES_PATTERN.matcher(cleaned).replaceAll(" ");

        // Trim
        cleaned = cleaned.trim();

        return cleaned;
    }

    /**
     * Extract merchant name from UPI transaction description
     */
    private static String extractUpiMerchant(String upiDescription) {
        // Split by "/" or "-" and try to find merchant name
        String[] parts = upiDescription.split("[/-]");

        for (String part : parts) {
            part = part.trim();
            // Skip if it's just "upi", numbers, or very short
            if (!part.equals("upi") && !part.matches("\\d+") && part.length() > 2) {
                // Remove underscores and take first meaningful word
                String[] words = part.split("[_\\s]");
                for (String word : words) {
                    if (word.length() > 2 && !word.matches("\\d+")) {
                        return word;
                    }
                }
            }
        }

        return upiDescription;
    }

    /**
     * Extract merchant name from POS transaction description
     */
    private static String extractPosMerchant(String posDescription) {
        // Remove "POS" prefix and numbers, then take first meaningful word
        String cleaned = posDescription.replaceFirst("^pos\\s*", "");
        cleaned = NUMBERS_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = cleaned.trim();

        String[] words = cleaned.split("\\s+");
        if (words.length > 0) {
            return words[0];
        }

        return posDescription;
    }

    /**
     * Extract key merchant name for better matching
     */
    public String extractMerchantKeyword(String description) {
        String cleaned = clean(description);

        // Return first meaningful word (longer than 2 chars)
        String[] words = cleaned.split("\\s+");
        for (String word : words) {
            if (word.length() > 2) {
                return word;
            }
        }

        return cleaned;
    }

    public static String normalize(String input) {
        return input
                .toLowerCase()
                .replace("-", " ")
                .trim();
    }
}

