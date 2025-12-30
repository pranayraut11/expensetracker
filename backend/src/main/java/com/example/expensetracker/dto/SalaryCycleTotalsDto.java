package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryCycleTotalsDto {
    private Double totalCredit;
    private Double totalDebit;
    private Double netSavings;
    private Double salaryAmount;
}

