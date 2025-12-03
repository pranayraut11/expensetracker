package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pageable response wrapper for transaction list
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedTransactionResponse {
    private List<TransactionDto> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}

