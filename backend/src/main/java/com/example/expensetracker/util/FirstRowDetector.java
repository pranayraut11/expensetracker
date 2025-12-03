package com.example.expensetracker.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class FirstRowDetector {

    // Date patterns to match
    private static final Pattern DATE_PATTERN_DDMMYYYY = Pattern.compile("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}");
    private static final Pattern DATE_PATTERN_YYYYMMDD = Pattern.compile("\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}");

    // Header keywords to exclude
    private static final String[] HEADER_KEYWORDS = {
        "DATE", "NARRATION", "DESCRIPTION", "WITHDRAWAL", "DEPOSIT", "AMOUNT",
        "BALANCE", "CREDIT", "DEBIT", "TRANSACTION", "PARTICULARS", "CHQ",
        "REF", "VALUE", "CLOSING", "OPENING", "SR", "S.NO", "SL.NO"
    };

    // Summary keywords to exclude
    private static final String[] SUMMARY_KEYWORDS = {
        "OPENING BALANCE", "CLOSING BALANCE", "TOTAL", "GRAND TOTAL",
        "SUMMARY", "BALANCE B/F", "BALANCE C/F", "SUBTOTAL"
    };

    /**
     * Detect if this is the first transaction row in Excel
     *
     * @param row Excel row
     * @return true if this is a transaction row
     */
    public static boolean isFirstTransactionRow(Row row) {
        if (row == null) {
            return false;
        }

        // Check if row has a valid date in first few cells
        boolean hasDate = false;
        boolean hasAmount = false;
        boolean isHeader = false;
        boolean isSummary = false;

        // Check cells
        for (int i = 0; i < Math.min(row.getLastCellNum(), 10); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }

            String cellValue = getCellValueAsString(cell);

            // Check for header keywords
            if (isHeaderRow(cellValue)) {
                isHeader = true;
                break;
            }

            // Check for summary keywords
            if (isSummaryRow(cellValue)) {
                isSummary = true;
                break;
            }

            // Check for date
            if (!hasDate && isValidDate(cell)) {
                hasDate = true;
            }

            // Check for amount (numeric value > 0)
            if (!hasAmount && isNumericAmount(cell)) {
                hasAmount = true;
            }
        }

        // First transaction row must:
        // 1. Have a valid date
        // 2. Have at least one numeric amount
        // 3. NOT be a header row
        // 4. NOT be a summary row
        return hasDate && hasAmount && !isHeader && !isSummary;
    }

    /**
     * Detect if this is the first transaction row from text line (for PDF)
     *
     * @param line Text line from PDF
     * @return true if this is a transaction row
     */
    public static boolean isFirstTransactionRow(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }

        String upperLine = line.toUpperCase();

        // Check if it's a header row
        if (isHeaderRow(upperLine)) {
            return false;
        }

        // Check if it's a summary row
        if (isSummaryRow(upperLine)) {
            return false;
        }

        // Check if line contains a date pattern
        boolean hasDate = DATE_PATTERN_DDMMYYYY.matcher(line).find() ||
                         DATE_PATTERN_YYYYMMDD.matcher(line).find();

        // Check if line contains numeric values (amounts)
        boolean hasAmount = line.matches(".*\\d+\\.?\\d*.*");

        return hasDate && hasAmount;
    }

    /**
     * Check if row/line is a header
     */
    private static boolean isHeaderRow(String value) {
        if (value == null) {
            return false;
        }

        String upper = value.toUpperCase().trim();

        // Check against header keywords
        for (String keyword : HEADER_KEYWORDS) {
            if (upper.equals(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if row/line is a summary row
     */
    private static boolean isSummaryRow(String value) {
        if (value == null) {
            return false;
        }

        String upper = value.toUpperCase().trim();

        for (String keyword : SUMMARY_KEYWORDS) {
            if (upper.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if cell contains a valid date
     */
    private static boolean isValidDate(Cell cell) {
        if (cell == null) {
            return false;
        }

        // Check if cell is a date type
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return true;
        }

        // Check if cell contains date pattern as string
        String cellValue = getCellValueAsString(cell);
        if (cellValue != null && !cellValue.trim().isEmpty()) {
            // Try to match date patterns
            if (DATE_PATTERN_DDMMYYYY.matcher(cellValue).matches() ||
                DATE_PATTERN_YYYYMMDD.matcher(cellValue).matches()) {
                return tryParseDate(cellValue);
            }
        }

        return false;
    }

    /**
     * Try to parse date string
     */
    private static boolean tryParseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        String cleaned = dateStr.trim().replace("/", "-");

        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate.parse(cleaned, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        return false;
    }

    /**
     * Check if cell contains a numeric amount
     */
    private static boolean isNumericAmount(Cell cell) {
        if (cell == null) {
            return false;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            double value = cell.getNumericCellValue();
            return value > 0; // Must be positive amount
        }

        // Try to parse as number from string
        String cellValue = getCellValueAsString(cell);
        if (cellValue != null) {
            try {
                // Remove commas and parse
                String cleaned = cellValue.replace(",", "").trim();
                double value = Double.parseDouble(cleaned);
                return value > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * Get cell value as string
     */
    private static String getCellValueAsString(Cell cell) {
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
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}

