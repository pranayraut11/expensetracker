package com.example.expensetracker.parser;

import com.example.expensetracker.model.BankType;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.service.BankDetectorService;
import com.example.expensetracker.service.DynamicDroolsService;
import com.example.expensetracker.util.FirstRowDetector;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SmartExcelParser implements StatementParser {

    private static final Logger logger = LoggerFactory.getLogger(SmartExcelParser.class);

    private final BankDetectorService bankDetectorService;
    private final DynamicDroolsService dynamicDroolsService;
    private BankType detectedBank = BankType.UNKNOWN;

    public SmartExcelParser(BankDetectorService bankDetectorService, DynamicDroolsService dynamicDroolsService) {
        this.bankDetectorService = bankDetectorService;
        this.dynamicDroolsService = dynamicDroolsService;
    }

    @Override
    public List<Transaction> parse(MultipartFile file) throws IOException {
        return parse(file, null);
    }

    @Override
    public List<Transaction> parse(MultipartFile file, String password) throws IOException {
        // Excel files don't typically use password protection in our use case
        // Password parameter is ignored for Excel files
        List<Transaction> transactions = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Step 1: Detect bank from header rows
            List<String> headerLines = extractHeaderLines(sheet, 5);
            detectedBank = bankDetectorService.detectBank(headerLines);
            logger.info("Detected bank: {}", detectedBank.getDisplayName());

            // Step 2: Find first transaction row
            int firstTransactionRowIndex = findFirstTransactionRow(sheet);
            if (firstTransactionRowIndex == -1) {
                logger.warn("No transaction rows found in Excel file");
                return transactions;
            }

            logger.info("First transaction row found at index: {}", firstTransactionRowIndex);

            // Step 3: Detect column indices
            Row headerRow = sheet.getRow(firstTransactionRowIndex - 1);
            if (headerRow == null) {
                headerRow = sheet.getRow(firstTransactionRowIndex);
            }

            ColumnIndices columns = detectColumnIndices(headerRow);

            // Step 4: Parse transactions
            for (int i = firstTransactionRowIndex; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
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

        logger.info("Parsed {} transactions from Excel", transactions.size());
        return transactions;
    }

    @Override
    public boolean supports(String filename) {
        return filename != null && (filename.toLowerCase().endsWith(".xlsx") ||
                                    filename.toLowerCase().endsWith(".xls"));
    }

    @Override
    public BankType getDetectedBank() {
        return detectedBank;
    }

    /**
     * Extract header lines for bank detection
     */
    private List<String> extractHeaderLines(Sheet sheet, int numLines) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < Math.min(numLines, sheet.getLastRowNum() + 1); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                StringBuilder sb = new StringBuilder();
                for (Cell cell : row) {
                    String value = getCellValueAsString(cell);
                    if (value != null && !value.trim().isEmpty()) {
                        sb.append(value).append(" ");
                    }
                }
                lines.add(sb.toString().trim());
            }
        }
        return lines;
    }

    /**
     * Find the index of first transaction row
     */
    private int findFirstTransactionRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(50, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (FirstRowDetector.isFirstTransactionRow(row)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Detect column indices based on header row
     */
    private ColumnIndices detectColumnIndices(Row headerRow) {
        ColumnIndices indices = new ColumnIndices();

        if (headerRow == null) {
            // Use default indices
            indices.dateCol = 0;
            indices.descriptionCol = 1;
            indices.refNoCol = 2;
            indices.withdrawalCol = 4;
            indices.depositCol = 5;
            indices.balanceCol = 6;
            return indices;
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            String header = getCellValueAsString(cell);
            if (header == null) {
                continue;
            }

            String upperHeader = header.toUpperCase().trim();

            // Detect Date column
            if (upperHeader.contains("DATE") && !upperHeader.contains("VALUE")) {
                indices.dateCol = i;
            }

            // Detect Description/Narration column
            if (upperHeader.contains("DESCRIPTION") || upperHeader.contains("NARRATION") ||
                upperHeader.contains("PARTICULARS")) {
                indices.descriptionCol = i;
            }

            // Detect Reference Number column
            if (upperHeader.contains("REF") || upperHeader.contains("CHQ") ||
                upperHeader.contains("CHEQUE")) {
                indices.refNoCol = i;
            }

            // Detect Withdrawal column
            if (upperHeader.contains("WITHDRAWAL") || upperHeader.contains("DEBIT") ||
                upperHeader.contains("DR ")) {
                indices.withdrawalCol = i;
            }

            // Detect Deposit column
            if (upperHeader.contains("DEPOSIT") || upperHeader.contains("CREDIT") ||
                upperHeader.contains("CR ")) {
                indices.depositCol = i;
            }

            // Detect Balance column
            if (upperHeader.contains("BALANCE") || upperHeader.contains("CLOSING")) {
                indices.balanceCol = i;
            }
        }

        logger.info("Detected columns: date={}, desc={}, ref={}, withdrawal={}, deposit={}, balance={}",
                   indices.dateCol, indices.descriptionCol, indices.refNoCol,
                   indices.withdrawalCol, indices.depositCol, indices.balanceCol);

        return indices;
    }

    /**
     * Parse a single row into Transaction
     */
    private Transaction parseRow(Row row, ColumnIndices columns) {
        // Parse date
        LocalDate date = parseDate(row.getCell(columns.dateCol));
        if (date == null) {
            return null; // Skip rows without valid date
        }

        // Parse description
        String description = getCellValueAsString(row.getCell(columns.descriptionCol));
        if (description == null || description.trim().isEmpty()) {
            return null; // Skip rows without description
        }

        // Parse reference number
        String refNo = getCellValueAsString(row.getCell(columns.refNoCol));

        // Parse amounts
        Double withdrawal = parseAmount(row.getCell(columns.withdrawalCol));
        Double deposit = parseAmount(row.getCell(columns.depositCol));

        // Parse balance
        Double balance = parseAmount(row.getCell(columns.balanceCol));

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setRefNo(refNo);
        transaction.setBalance(balance);

        // Determine type and amount
        if (withdrawal != null && withdrawal > 0) {
            transaction.setType("DEBIT");
            transaction.setAmount(Math.abs(withdrawal));
        } else if (deposit != null && deposit > 0) {
            transaction.setType("CREDIT");
            transaction.setAmount(Math.abs(deposit));
        } else {
            return null; // Skip rows without amount
        }

        // Check if this is a credit card payment transaction
        boolean isCreditCardPayment = isCreditCardPaymentDescription(description);

        // Set credit card related fields
        transaction.setIsCreditCardTransaction(false); // This is a bank transaction
        transaction.setIsCreditCardPayment(isCreditCardPayment);
        transaction.setIncludeInTotals(!isCreditCardPayment); // Exclude CC payments from totals

        // If it's a CC payment, set category to Transfers
        if (isCreditCardPayment) {
            transaction.setCategory("Transfers");
        } else {
            transaction.setCategory("Miscellaneous"); // Will be overridden by rules
        }

        return transaction;
    }

    /**
     * Check if description indicates a credit card payment
     */
    private boolean isCreditCardPaymentDescription(String description) {
        if (description == null) {
            return false;
        }

        String upper = description.toUpperCase();
        return upper.contains("CREDIT CARD PAYMENT") ||
               upper.contains("CC PAYMENT") ||
               upper.contains("VISA PAYMENT") ||
               upper.contains("AMEX PAYMENT") ||
               upper.contains("MASTERCARD PAYMENT") ||
               upper.contains("CARD PAYMENT") ||
               upper.contains("CREDITCARD") ||
               (upper.contains("CREDIT") && upper.contains("CARD") && upper.contains("PAYMENT"));
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
                    // Remove commas and parse
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
                // Format number without scientific notation
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
     * Helper class to store column indices
     */
    private static class ColumnIndices {
        int dateCol = 0;
        int descriptionCol = 1;
        int refNoCol = 2;
        int withdrawalCol = 4;
        int depositCol = 5;
        int balanceCol = 6;
    }
}
