package com.example.expensetracker.service;

import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.util.FingerprintHashUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CreditCardXLSParser {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardXLSParser.class);

    private final DynamicDroolsService dynamicDroolsService;

    public CreditCardXLSParser(DynamicDroolsService dynamicDroolsService) {
        this.dynamicDroolsService = dynamicDroolsService;
    }

    /**
     * Parse credit card statement XLS file
     * Expected columns: Transaction Type | Customer Name | Date | Description | Amount | Debit/Credit
     */
    public List<Transaction> parseXLS(MultipartFile file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Find header row and detect columns
            int headerRow = findHeaderRow(sheet);
            if (headerRow == -1) {
                throw new IOException("Could not find header row in XLS file");
            }

            ColumnIndices columns = detectColumns(sheet.getRow(headerRow));
            logger.info("Detected columns: date={}, desc={}, amount={}, debitCredit={}",
                       columns.dateCol, columns.descCol, columns.amountCol, columns.debitCreditCol);

            // Parse data rows
            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                try {
                    Transaction transaction = parseRow(row, columns);
                    if (transaction != null) {
                        // Apply categorization rules
                        dynamicDroolsService.applyRules(transaction);
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse row {}: {}", i, e.getMessage());
                }
            }
        }

        logger.info("Parsed {} credit card transactions from XLS", transactions.size());
        return transactions;
    }

    /**
     * Find header row by looking for "Date" or "Description" column
     */
    private int findHeaderRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(40, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            for (Cell cell : row) {
                String value = getCellValueAsString(cell);
                if (value != null) {
                    String upper = value.toUpperCase().trim();
                    if (upper.equals("DATE") || upper.equals("DESCRIPTION") || upper.equals("AMT")) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Detect column indices from header row
     */
    private ColumnIndices detectColumns(Row headerRow) {
        ColumnIndices indices = new ColumnIndices();

        if (headerRow == null) {
            // Default indices if header not found
            indices.dateCol = 2;
            indices.descCol = 3;
            indices.amountCol = 4;
            indices.debitCreditCol = 5;
            return indices;
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String header = getCellValueAsString(cell);
            if (!StringUtils.hasText(header)) continue;

            String upper = header.toUpperCase().trim();

            switch (upper) {
                case "DATE" -> indices.dateCol = i;
                case "DESCRIPTION" -> indices.descCol = i;
                case "AMT" -> indices.amountCol = i;
                case "DEBIT / CREDIT" -> indices.debitCreditCol = i;
            }
        }

        return indices;
    }

    /**
     * Parse a single row into Transaction
     */
    private Transaction parseRow(Row row, ColumnIndices columns) {
        // Parse date
        LocalDate date = parseDate(row.getCell(columns.dateCol));
        if (date == null) {
            return null;
        }

        // Parse description
        String description = getCellValueAsString(row.getCell(columns.descCol));
        if (description == null || description.trim().isEmpty()) {
            return null;
        }

        // Parse amount
        Double amount = parseAmount(row.getCell(columns.amountCol));
        if (amount == null || amount == 0) {
            return null;
        }

        // Parse debit/credit indicator
        String debitCreditIndicator = getCellValueAsString(row.getCell(columns.debitCreditCol));

        // Determine transaction type
        String type = determineType(description, debitCreditIndicator, amount);

        // Make amount always positive
        amount = Math.abs(amount);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory("Miscellaneous"); // Will be overridden by rules

        // Mark as credit card transaction
        transaction.setIsCreditCardTransaction(true);
        transaction.setIsCreditCardPayment(false);
        transaction.setIncludeInTotals(true);

        // Generate fingerprint hash
        String fingerprint = FingerprintHashUtil.generateFingerprint(date, description, amount, type);
        transaction.setFingerprintHash(fingerprint);

        // Also generate transactionHash for consistency
        transaction.setTransactionHash(fingerprint); // Using same hash for now

        return transaction;
    }

    /**
     * Determine transaction type based on description and indicator
     */
    private String determineType(String description, String indicator, Double amount) {
        // Check indicator first
        if (indicator != null) {
            String upper = indicator.toUpperCase().trim();
            if (upper.contains("CR") || upper.equals("CREDIT")) {
                return "CREDIT";
            }
            if (upper.contains("DR") || upper.equals("DEBIT")) {
                return "DEBIT";
            }
        }

        // Check description for credit keywords
        String descUpper = description.toUpperCase();
        if (descUpper.contains("CREDIT") || descUpper.contains("REFUND") ||
            descUpper.contains("REVERSAL") || descUpper.contains("CASHBACK")) {
            return "CREDIT";
        }

        // Default: positive amount = expense (DEBIT for credit card)
        return amount > 0 ? "DEBIT" : "CREDIT";
    }

    /**
     * Parse date from cell
     */
    private LocalDate parseDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                String dateStr = getCellValueAsString(cell);
                return parseDateString(dateStr);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse date: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse date string with multiple formats
     */
    private LocalDate parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        String cleaned = dateStr.trim().replace("/", "-");

        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(cleaned, formatter);
            } catch (Exception e) {
                // Try next formatter
            }
        }

        return null;
    }

    /**
     * Parse amount from cell
     */
    private Double parseAmount(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                String amountStr = getCellValueAsString(cell);
                if (amountStr != null && !amountStr.trim().isEmpty()) {
                    String cleaned = amountStr.replace(",", "").trim();
                    return Double.parseDouble(cleaned);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse amount: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }

    /**
     * Check if row is empty
     */
    private boolean isEmptyRow(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper class to store column indices
     */
    private static class ColumnIndices {
        int dateCol = 2;
        int descCol = 3;
        int amountCol = 4;
        int debitCreditCol = 5;
    }
}

