package com.example.expensetracker.service;

import com.example.expensetracker.model.BankType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankDetectorService {

    /**
     * Detect bank type from file content (header rows)
     *
     * @param headerLines First few lines/rows from the file
     * @return Detected BankType
     */
    public BankType detectBank(List<String> headerLines) {
        if (headerLines == null || headerLines.isEmpty()) {
            return BankType.UNKNOWN;
        }

        // Combine all header lines into one string for searching
        String combinedHeader = String.join(" ", headerLines).toUpperCase();

        // Try to match each bank type
        for (BankType bankType : BankType.values()) {
            if (bankType == BankType.UNKNOWN) {
                continue;
            }

            for (String identifier : bankType.getIdentifiers()) {
                if (combinedHeader.contains(identifier.toUpperCase())) {
                    return bankType;
                }
            }
        }

        return BankType.UNKNOWN;
    }

    /**
     * Detect bank type from a single string (useful for PDF single-line headers)
     */
    public BankType detectBank(String headerContent) {
        if (headerContent == null || headerContent.trim().isEmpty()) {
            return BankType.UNKNOWN;
        }

        String content = headerContent.toUpperCase();

        for (BankType bankType : BankType.values()) {
            if (bankType == BankType.UNKNOWN) {
                continue;
            }

            for (String identifier : bankType.getIdentifiers()) {
                if (content.contains(identifier.toUpperCase())) {
                    return bankType;
                }
            }
        }

        return BankType.UNKNOWN;
    }
}

