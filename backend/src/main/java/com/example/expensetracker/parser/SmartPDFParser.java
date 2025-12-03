package com.example.expensetracker.parser;

import com.example.expensetracker.model.BankType;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.service.BankDetectorService;
import com.example.expensetracker.service.DynamicDroolsService;
import com.example.expensetracker.util.FirstRowDetector;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// @Component - Removed: PDF parsing not supported
public class SmartPDFParser implements StatementParser {

    private static final Logger logger = LoggerFactory.getLogger(SmartPDFParser.class);

    // Date patterns
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})");

    // Amount patterns (with commas and decimals)
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("([\\d,]+\\.\\d{2})");

    private final BankDetectorService bankDetectorService;
    private final DynamicDroolsService dynamicDroolsService;
    private BankType detectedBank = BankType.UNKNOWN;

    public SmartPDFParser(BankDetectorService bankDetectorService, DynamicDroolsService dynamicDroolsService) {
        this.bankDetectorService = bankDetectorService;
        this.dynamicDroolsService = dynamicDroolsService;
    }

    @Override
    public List<Transaction> parse(MultipartFile file) throws IOException {
        return parse(file, null);
    }

    @Override
    public List<Transaction> parse(MultipartFile file, String password) throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        PDDocument document = null;
        try {
            // Load PDF document with password support
            if (password != null && !password.trim().isEmpty()) {
                logger.info("Attempting to open password-protected PDF");
                document = PDDocument.load(file.getInputStream(), password.trim());
            } else {
                document = PDDocument.load(file.getInputStream());
            }

            // Check if document is encrypted and we don't have password
            if (document.isEncrypted() && (password == null || password.trim().isEmpty())) {
                throw new IOException("PDF is password-protected. Please provide the password.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Split into lines
            String[] lines = text.split("\n");

            // Step 1: Detect bank from first few lines
            List<String> headerLines = new ArrayList<>();
            for (int i = 0; i < Math.min(10, lines.length); i++) {
                headerLines.add(lines[i]);
            }
            detectedBank = bankDetectorService.detectBank(headerLines);
            logger.info("Detected bank from PDF: {}", detectedBank.getDisplayName());

            // Step 2: Find first transaction line
            int firstTransactionIndex = -1;
            for (int i = 0; i < Math.min(30, lines.length); i++) {
                if (FirstRowDetector.isFirstTransactionRow(lines[i])) {
                    firstTransactionIndex = i;
                    logger.info("First transaction found at line: {}", i);
                    break;
                }
            }

            if (firstTransactionIndex == -1) {
                logger.warn("No transaction rows found in PDF");
                return transactions;
            }

            // Step 3: Parse transactions
            for (int i = firstTransactionIndex; i < lines.length; i++) {
                String line = lines[i];

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Skip summary lines
                if (FirstRowDetector.isFirstTransactionRow(line)) {
                    try {
                        Transaction transaction = parseLine(line);
                        if (transaction != null) {
                            // Apply categorization rules
                            dynamicDroolsService.applyRules(transaction);
                            transactions.add(transaction);
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to parse PDF line {}: {}", i, e.getMessage());
                    }
                } else if (!line.toUpperCase().contains("OPENING") &&
                          !line.toUpperCase().contains("CLOSING") &&
                          !line.toUpperCase().contains("TOTAL")) {
                    // Skip non-transaction lines
                    continue;
                }
            }
        } catch (org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException e) {
            logger.error("Invalid password provided for PDF");
            throw new IOException("Invalid password. Please check your password and try again.");
        } catch (IOException e) {
            logger.error("Error reading PDF: {}", e.getMessage());
            throw e;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.warn("Error closing PDF document: {}", e.getMessage());
                }
            }
        }

        logger.info("Parsed {} transactions from PDF", transactions.size());
        return transactions;
    }

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }

    @Override
    public BankType getDetectedBank() {
        return detectedBank;
    }

    /**
     * Parse a single line from PDF into Transaction
     */
    private Transaction parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        // Parse date
        LocalDate date = parseDate(line);
        if (date == null) {
            return null;
        }

        // Extract all amounts from the line
        List<Double> amounts = extractAmounts(line);
        if (amounts.isEmpty()) {
            return null;
        }

        // Parse based on common bank statement formats
        // Format: Date | Description | RefNo | Withdrawal | Deposit | Balance
        // OR: Date | Description | Amount | Balance

        Transaction transaction = new Transaction();
        transaction.setDate(date);

        // Extract description (text between date and amounts)
        String description = extractDescription(line);
        transaction.setDescription(description);

        // Extract reference number (if present)
        String refNo = extractRefNo(line);
        transaction.setRefNo(refNo);

        // Determine transaction type and amount
        // Usually: last amount = balance, second-to-last = transaction amount
        if (amounts.size() >= 2) {
            Double balance = amounts.get(amounts.size() - 1);
            Double amount = amounts.get(amounts.size() - 2);

            transaction.setBalance(balance);
            transaction.setAmount(Math.abs(amount));

            // Determine if DEBIT or CREDIT based on amount and balance
            // If balance decreased, it's a debit; if increased, it's credit
            // But we'll use a simpler heuristic: check for keywords
            String upperLine = line.toUpperCase();
            if (upperLine.contains("CR") || upperLine.contains("CREDIT") ||
                upperLine.contains("SALARY") || upperLine.contains("NEFT CR")) {
                transaction.setType("CREDIT");
            } else {
                transaction.setType("DEBIT");
            }
        } else if (amounts.size() == 1) {
            // Only one amount - could be balance or transaction amount
            transaction.setAmount(Math.abs(amounts.get(0)));
            transaction.setBalance(amounts.get(0));
            transaction.setType("DEBIT"); // Default
        } else {
            return null;
        }

        // Set default category
        transaction.setCategory("Miscellaneous");

        return transaction;
    }

    /**
     * Parse date from line
     */
    private LocalDate parseDate(String line) {
        Matcher matcher = DATE_PATTERN.matcher(line);
        if (matcher.find()) {
            String dateStr = matcher.group(1);
            return parseDateString(dateStr);
        }
        return null;
    }

    /**
     * Parse date string with support for multiple formats
     */
    private LocalDate parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        String cleaned = dateStr.trim()
            .replace("/", "-")
            .replace(".", "-")
            .replaceAll("\\s+", "");

        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yy"),
            DateTimeFormatter.ofPattern("d-MMM-yyyy"),
            DateTimeFormatter.ofPattern("MMM-dd-yyyy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(cleaned, formatter);
            } catch (Exception e) {
                // Try next formatter
            }
        }

        logger.debug("Could not parse date: {}", dateStr);
        return null;
    }

    /**
     * Extract all amounts from line
     */
    private List<Double> extractAmounts(String line) {
        List<Double> amounts = new ArrayList<>();
        Matcher matcher = AMOUNT_PATTERN.matcher(line);

        while (matcher.find()) {
            String amountStr = matcher.group(1).replace(",", "");
            try {
                amounts.add(Double.parseDouble(amountStr));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse amount: {}", amountStr);
            }
        }

        return amounts;
    }

    /**
     * Extract description from line
     */
    private String extractDescription(String line) {
        // Remove date pattern
        String desc = DATE_PATTERN.matcher(line).replaceAll("");

        // Remove amount patterns
        desc = AMOUNT_PATTERN.matcher(desc).replaceAll("");

        // Clean up extra spaces
        desc = desc.trim().replaceAll("\\s+", " ");

        return desc;
    }

    /**
     * Extract reference number from line
     */
    private String extractRefNo(String line) {
        // Look for patterns like: Ref No: XXXXX, CHQ: XXXXX, etc.
        Pattern refPattern = Pattern.compile("(?:REF|CHQ|CHEQUE|REF\\.?\\s*NO)[:\\s]+(\\d+)");
        Matcher matcher = refPattern.matcher(line.toUpperCase());

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Look for standalone long numbers (possible ref numbers)
        Pattern longNumPattern = Pattern.compile("(\\d{10,})");
        matcher = longNumPattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;

      }
}

