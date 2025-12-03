package com.example.expensetracker.controller;

import com.example.expensetracker.dto.UploadResponseDto;
import com.example.expensetracker.service.SmartUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final SmartUploadService smartUploadService;

    @Autowired
    public UploadController(SmartUploadService smartUploadService) {
        this.smartUploadService = smartUploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            logger.info("Received file upload request: {}", file.getOriginalFilename());

            // Process upload using smart service
            UploadResponseDto response = smartUploadService.processUpload(file);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error processing file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }
}
