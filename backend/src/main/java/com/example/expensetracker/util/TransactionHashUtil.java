package com.example.expensetracker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class TransactionHashUtil {

    /**
     * Generate SHA-256 hash from transaction fields
     * Hash is based on: description + refNo + date + amount + type
     *
     * @param description Transaction description
     * @param refNo Reference number
     * @param date Transaction date
     * @param amount Transaction amount
     * @param type Transaction type (DEBIT/CREDIT)
     * @return SHA-256 hash string
     */
    public static String generateHash(String description, String refNo, LocalDate date, Double amount, String type) {
        try {
            // Build string to hash
            StringBuilder sb = new StringBuilder();
            sb.append(description != null ? description.trim() : "");
            sb.append("|");
            sb.append(refNo != null ? refNo.trim() : "");
            sb.append("|");
            sb.append(date != null ? date.toString() : "");
            sb.append("|");
            sb.append(amount != null ? amount.toString() : "");
            sb.append("|");
            sb.append(type != null ? type.trim().toUpperCase() : "");

            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal string
            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
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

