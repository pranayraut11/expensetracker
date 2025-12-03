package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDto {
    private int successCount;
    private int errorCount;
    private int skippedCount;
    private String message;
}

