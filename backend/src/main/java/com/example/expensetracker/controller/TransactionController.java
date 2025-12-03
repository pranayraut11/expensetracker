package com.example.expensetracker.controller;

import com.example.expensetracker.dto.PagedTransactionResponse;
import com.example.expensetracker.dto.SummaryDto;
import com.example.expensetracker.dto.TagSuggestionDto;
import com.example.expensetracker.dto.TransactionDto;
import com.example.expensetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get all transactions with pagination, filtering, and sorting
     *
     * Query Params:
     * - page: Page number (default 0)
     * - size: Page size (default 20)
     * - sort: Sort field and direction (default "date,desc")
     *         Format: field,direction
     *         Examples: date,desc | amount,asc | category,asc
     * - search: Search description (optional)
     * - category: Filter by category (optional)
     * - type: Filter by type CREDIT/DEBIT (optional)
     * - isCreditCardTransaction: Filter by CC flag (optional)
     * - fromDate: Start date for range filter (optional, requires toDate)
     * - toDate: End date for range filter (optional, requires fromDate)
     *
     * ALWAYS excludes transactions with category = "Credit Card Payment"
     */
    @GetMapping
    public ResponseEntity<PagedTransactionResponse> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isCreditCardTransaction,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        // Parse sort parameter
        String[] sortParts = sort.split(",");
        String sortField = sortParts.length > 0 ? sortParts[0] : "date";
        String sortDirection = sortParts.length > 1 ? sortParts[1] : "desc";

        // Validate and map sort field
        sortField = mapSortField(sortField);

        PagedTransactionResponse response = transactionService.getTransactionsPageable(
            page, size, sortField, sortDirection,
            search, category, type, isCreditCardTransaction,
            fromDate, toDate
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Map frontend sort field names to entity field names
     */
    private String mapSortField(String field) {
        return switch (field.toLowerCase()) {
            case "date" -> "date";
            case "amount" -> "amount";
            case "category", "categoryname" -> "category";
            case "description" -> "description";
            case "type" -> "type";
            default -> "date"; // default fallback
        };
    }

    /**
     * Get summary statistics with optional filters
     * Uses repository queries with includeInTotals filtering to exclude credit card payments
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryDto> getSummary(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        SummaryDto summary = transactionService.getSummary(category, fromDate, toDate);
        return ResponseEntity.ok(summary);
    }

    /**
     * Update category for a specific transaction
     */
    @PutMapping("/{id}/category")
    public ResponseEntity<TransactionDto> updateTransactionCategory(
            @PathVariable("id") Long id,
            @RequestBody String category) {
        TransactionDto updated = transactionService.updateCategory(id, category);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * Get top tag suggestions for rule creation
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagSuggestionDto>> getTopTags(@RequestParam(defaultValue = "25") int limit) {
        return ResponseEntity.ok(transactionService.getTopTags(limit));
    }
}

