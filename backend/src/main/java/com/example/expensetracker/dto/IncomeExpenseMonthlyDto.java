package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeExpenseMonthlyDto {
    private String month; // Format: "2024-01"
    private Double income;
    private Double expenses;
}

