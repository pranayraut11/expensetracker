package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDto {
    private int rowsProcessed;
    private int rowsSaved;
    private int errors;
    private int duplicates;
    private List<String> duplicateTransactions;

    public UploadResponseDto(int rowsProcessed, int rowsSaved, int errors) {
        this.rowsProcessed = rowsProcessed;
        this.rowsSaved = rowsSaved;
        this.errors = errors;
        this.duplicates = 0;
        this.duplicateTransactions = new ArrayList<>();
    }
}

