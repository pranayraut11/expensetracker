package com.example.expensetracker.service;

import com.example.expensetracker.parser.SmartExcelParser;
import com.example.expensetracker.parser.StatementParser;
import org.springframework.stereotype.Service;

@Service
public class ParserFactory {

    private final SmartExcelParser excelParser;

    public ParserFactory(SmartExcelParser excelParser) {
        this.excelParser = excelParser;
    }

    /**
     * Get appropriate parser based on filename (Excel only)
     *
     * @param filename Name of the uploaded file
     * @return StatementParser instance
     * @throws IllegalArgumentException if file type not supported
     */
    public StatementParser getParser(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".xlsx") || lowerFilename.endsWith(".xls")) {
            return excelParser;
        } else {
            throw new IllegalArgumentException(
                "Unsupported file type. Please upload Excel (.xlsx or .xls) files only."
            );
        }
    }

    /**
     * Check if file type is supported (Excel only)
     */
    public boolean isSupported(String filename) {
        if (filename == null) {
            return false;
        }

        String lower = filename.toLowerCase();
        return lower.endsWith(".xlsx") || lower.endsWith(".xls");
    }
}

