package com.example.expensetracker.service;

import com.example.expensetracker.dto.TransactionSaveResult;
import com.example.expensetracker.dto.UploadResponseDto;
import com.example.expensetracker.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CreditCardStatementService {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardStatementService.class);

    private final CreditCardXLSParser xlsParser;
    private final TransactionService transactionService;

    public CreditCardStatementService(CreditCardXLSParser xlsParser, TransactionService transactionService) {
        this.xlsParser = xlsParser;
        this.transactionService = transactionService;
    }

    /**
     * Process credit card statement upload
     */
    public UploadResponseDto processUpload(MultipartFile file) throws Exception {
        // Validate file
        validateFile(file);

        String filename = file.getOriginalFilename();
        logger.info("Processing credit card statement file: {}", filename);

        // Parse XLS file
        List<Transaction> transactions = xlsParser.parseXLS(file);
        int rowsProcessed = transactions.size();

        logger.info("Parsed {} credit card transactions", rowsProcessed);

        // Save transactions with duplicate detection
        TransactionSaveResult saveResult = transactionService.saveAllWithDuplicateCheck(transactions);
        int rowsSaved = saveResult.getSavedTransactions().size();
        int duplicates = saveResult.getDuplicateTransactions().size();
        int errors = rowsProcessed - rowsSaved - duplicates;

        logger.info("Save completed: {} saved, {} duplicates, {} errors", rowsSaved, duplicates, errors);

        // Build response
        UploadResponseDto response = new UploadResponseDto(
            rowsProcessed,
            rowsSaved,
            errors,
            duplicates,
            saveResult.getDuplicateTransactions()
        );

        return response;
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename is invalid");
        }

        // Only accept Excel files
        if (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls")) {
            throw new IllegalArgumentException(
                "Unsupported file type. Please upload Excel (.xlsx or .xls) files only."
            );
        }

        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
    }
}

