package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {
    private double totalIncome;
    private double totalExpenses;
    private Map<String, Double> categoryBreakdown;
    private long transactionCount;
    private Double openingBalance;
    private Double closingBalance;
}
