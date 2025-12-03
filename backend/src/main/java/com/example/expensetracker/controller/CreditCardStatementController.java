package com.example.expensetracker.controller;

import com.example.expensetracker.dto.UploadResponseDto;
import com.example.expensetracker.service.CreditCardStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/credit-card")
public class CreditCardStatementController {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardStatementController.class);

    private final CreditCardStatementService creditCardStatementService;

    @Autowired
    public CreditCardStatementController(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }

    @PostMapping("/upload-xls")
    public ResponseEntity<?> uploadXLS(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received credit card statement upload request: {}", file.getOriginalFilename());

            // Process upload
            UploadResponseDto response = creditCardStatementService.processUpload(file);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing credit card statement upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }
}

