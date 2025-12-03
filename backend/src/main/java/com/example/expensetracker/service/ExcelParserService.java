package com.example.expensetracker.service;

import com.example.expensetracker.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ExcelParserService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelParserService.class);

    private final CategorizationService categorizationService;

    @Autowired
    public ExcelParserService(CategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    /**
     * Parse Excel file and return list of transactions
     */
    public List<Transaction> parse(MultipartFile file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name is null");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(inputStream, filename);
            Sheet sheet = workbook.getSheetAt(0); // Read first sheet

            // Read header and build column index map
            Map<String, Integer> headerIndex = new HashMap<>();
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                workbook.close();
                return transactions;
            }
            Row headerRow = rowIterator.next();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;
                String header = normalizeHeader(cell);
                if (header != null && !header.isEmpty()) {
                    headerIndex.put(header, i);
                }
            }

            int rowNum = 1; // data starts at second line
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNum++;

                if (isRowEmpty(row)) {
                    continue;
                }

                try {
                    Transaction transaction = parseRowFlexible(row, headerIndex);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing row {}: {}", rowNum, e.getMessage());
                }
            }

            workbook.close();
        }

        logger.info("Successfully parsed {} transactions from Excel file", transactions.size());
        return transactions;
    }

    /**
     * Create appropriate workbook based on file content (auto-detects .xls or .xlsx)
     */
    private Workbook createWorkbook(InputStream inputStream, String filename) throws IOException {
        try {
            // Use WorkbookFactory.create() which auto-detects the format
            // This works for both .xls (HSSF/OLE2) and .xlsx (XSSF/OOXML) formats
            return WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            logger.error("Failed to create workbook from file: {}", filename, e);
            throw new IllegalArgumentException(
                "Unsupported or corrupted Excel file format. Please ensure the file is a valid .xls or .xlsx file. Error: " + e.getMessage()
            );
        }
    }

    /**
     * Normalize header names to a canonical lowercase form without punctuation for mapping
     */
    private String normalizeHeader(Cell cell) {
        try {
            String raw = cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf(cell);
            if (raw == null) return null;
            String norm = raw.trim().toLowerCase()
                    .replaceAll("[*.]+", " ")
                    .replaceAll("\\s+", " ");
            // map common variants
            if (norm.contains("date") && !norm.contains("value")) return "date";
            if (norm.contains("value dt")) return "value_date";
            if (norm.contains("narration") || norm.contains("description")) return "description";
            if (norm.contains("withdrawal")) return "withdrawal";
            if (norm.contains("deposit")) return "deposit";
            if (norm.contains("balance")) return "balance";
            if (norm.equals("amount")) return "amount";
            if (norm.equals("type")) return "type";
            return norm;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse a row using header-driven mapping
     */
    private Transaction parseRowFlexible(Row row, Map<String, Integer> headerIndex) {
        // Determine format by presence of key headers
        boolean hasWD = headerIndex.containsKey("withdrawal");
        boolean hasDP = headerIndex.containsKey("deposit");

        Transaction transaction = new Transaction();

        if (hasWD || hasDP) {
            // Bank format with withdrawal/deposit
            LocalDate date = parseDateCell(getCell(row, headerIndex.getOrDefault("date", -1)));
            if (date == null) {
                date = parseDateCell(getCell(row, headerIndex.getOrDefault("value_date", -1)));
            }
            if (date == null) return null;

            String description = getStringCell(getCell(row, headerIndex.getOrDefault("description", -1)));
            if (description == null || description.trim().isEmpty()) return null;

            Double withdrawal = getNumericCell(getCell(row, headerIndex.getOrDefault("withdrawal", -1)));
            Double deposit = getNumericCell(getCell(row, headerIndex.getOrDefault("deposit", -1)));
            Double balance = getNumericCell(getCell(row, headerIndex.getOrDefault("balance", -1)));

            Double amount;
            String type;
            if (withdrawal != null && withdrawal != 0.0) {
                amount = withdrawal;
                type = "DEBIT";
            } else if (deposit != null && deposit != 0.0) {
                amount = deposit;
                type = "CREDIT";
            } else {
                return null;
            }

            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setType(type);
            transaction.setBalance(balance);
            transaction.setRefNo("");

        } else {
            // Simple format: Date | Description | Amount | Type | Balance
            LocalDate date = parseDateCell(getCell(row, headerIndex.getOrDefault("date", 0)));
            if (date == null) return null;

            String description = getStringCell(getCell(row, headerIndex.getOrDefault("description", 1)));
            if (description == null || description.trim().isEmpty()) return null;

            Double amount = getNumericCell(getCell(row, headerIndex.getOrDefault("amount", 2)));
            if (amount == null) return null;

            String type = getStringCell(getCell(row, headerIndex.getOrDefault("type", 3)));
            if (type == null || type.trim().isEmpty()) type = "DEBIT";

            Double balance = getNumericCell(getCell(row, headerIndex.getOrDefault("balance", 4)));

            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setAmount(amount);
            transaction.setType(type);
            transaction.setBalance(balance);
            transaction.setRefNo("");
        }

        // Apply categorization
        String category = categorizationService.categorize(transaction.getDescription(),transaction.getType());
        transaction.setCategory(category);

        return transaction;
    }

    private Cell getCell(Row row, int index) {
        if (index < 0 || index >= row.getLastCellNum()) return null;
        return row.getCell(index);
    }

    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();
                // Try parsing common date formats
                return parseStringDate(dateStr);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse date cell: {}", e.getMessage());
        }
        return null;
    }

    private LocalDate parseStringDate(String dateStr) {
        // Common date formats: dd-MM-yyyy, dd/MM/yyyy, yyyy-MM-dd
        String[] formats = {"dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "MM/dd/yyyy"};
        for (String format : formats) {
            try {
                return LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String getStringCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return String.valueOf((long) cell.getNumericCellValue());
            }
        } catch (Exception e) {
            logger.warn("Failed to read string cell: {}", e.getMessage());
        }
        return null;
    }

    private Double getNumericCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return null;
                // Remove currency symbols and commas
                str = str.replaceAll("[₹$,]", "").trim();
                return Double.parseDouble(str);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse numeric cell: {}", e.getMessage());
        }
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = cell.toString().trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}

