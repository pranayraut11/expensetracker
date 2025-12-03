package com.example.expensetracker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class FingerprintHashUtil {

    /**
     * Generate fingerprint hash for transaction deduplication
     * Format: date + "|" + normalizedDescription + "|" + amount + "|" + type
     *
     * @param date Transaction date
     * @param description Transaction description (will be normalized)
     * @param amount Transaction amount
     * @param type Transaction type (DEBIT/CREDIT)
     * @return SHA-256 fingerprint hash
     */
    public static String generateFingerprint(LocalDate date, String description, Double amount, String type) {
        try {
            // Normalize description (lowercase, trim, remove extra spaces)
            String normalizedDesc = normalizeDescription(description);

            // Build fingerprint string
            StringBuilder sb = new StringBuilder();
            sb.append(date != null ? date.toString() : "");
            sb.append("|");
            sb.append(normalizedDesc);
            sb.append("|");
            sb.append(amount != null ? String.format("%.2f", amount) : "0.00");
            sb.append("|");
            sb.append(type != null ? type.trim().toUpperCase() : "");

            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Normalize description for consistent hashing
     */
    private static String normalizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }

        return description.trim()
                .toLowerCase()
                .replaceAll("\\s+", " "); // Replace multiple spaces with single space
    }

    /**
     * Convert byte array to hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

