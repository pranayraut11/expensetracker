package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryCycleDto {
    private Long cycleId;
    private String label;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double salaryAmount;
}

