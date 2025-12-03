package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private LocalDate date;
    private String description;
    private String refNo;
    private Double amount;
    private String type;
    private Double balance;
    private String category;
    private Boolean isCreditCardTransaction;
    private Boolean isCreditCardPayment;
    private Boolean includeInTotals;
    private String transactionHash;
}

