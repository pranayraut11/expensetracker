package com.example.expensetracker.parser;

import com.example.expensetracker.model.BankType;
import com.example.expensetracker.model.Transaction;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StatementParser {

    /**
     * Parse statement file and extract transactions
     *
     * @param file MultipartFile uploaded by user
     * @return List of parsed transactions
     * @throws IOException if file reading fails
     */
    List<Transaction> parse(MultipartFile file) throws IOException;

    /**
     * Parse statement file and extract transactions with password support
     *
     * @param file MultipartFile uploaded by user
     * @param password Password for encrypted files (optional, can be null)
     * @return List of parsed transactions
     * @throws IOException if file reading fails
     */
    List<Transaction> parse(MultipartFile file, String password) throws IOException;

    /**
     * Check if this parser supports the given file type
     *
     * @param filename Name of the file
     * @return true if supported, false otherwise
     */
    boolean supports(String filename);

    /**
     * Get the detected bank type (if applicable)
     *
     * @return BankType detected from the file
     */
    BankType getDetectedBank();
}

