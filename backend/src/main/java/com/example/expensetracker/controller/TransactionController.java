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
     * Get all transactions with pagination, filtering, and multi-column sorting
     *
     * Query Params:
     * - page: Page number (default 0)
     * - size: Page size (default 20)
     * - sort: Sort field and direction (default "date,desc")
     *         Format: field,direction
     *         Examples: date,desc | amount,asc | category,asc
     *         Multi-sort: Can pass multiple sort parameters
     *         Examples: sort=type,asc&sort=category,asc&sort=date,desc
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
            @RequestParam(defaultValue = "date,desc") String[] sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isCreditCardTransaction,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        // Parse sort parameters - support multiple sort columns
        // Using String[] instead of List<String> to avoid automatic comma splitting
        // Using pipe (|) as delimiter instead of comma
        List<String[]> sortParams = java.util.Arrays.stream(sort)
            .map(s -> s.split("\\|"))  // Split on pipe instead of comma
            .toList();
        
        // Debug logging
        System.out.println("=== SORT DEBUG START ===");
        System.out.println("Raw sort params received: " + java.util.Arrays.toString(sort));
        System.out.println("sort.length: " + sort.length);
        for (int i = 0; i < sort.length; i++) {
            String[] param = sort[i].split("\\|");  // Split on pipe
            System.out.println("sort[" + i + "]: \"" + sort[i] + "\" -> split into " + java.util.Arrays.toString(param));
        }
        System.out.println("sortParams.size(): " + sortParams.size());
        for (int i = 0; i < sortParams.size(); i++) {
            String[] param = sortParams.get(i);
            System.out.println("sortParams[" + i + "]: length=" + param.length + ", values=" + java.util.Arrays.toString(param));
        }
        
        // Build sort field and direction for primary sort (first in list)
        String sortField = "date";
        String sortDirection = "desc";
        
        if (sortParams.size() > 0 && sortParams.get(0).length > 0) {
            String[] primary = sortParams.get(0);
            sortField = primary.length > 0 ? mapSortField(primary[0].trim()) : "date";
            sortDirection = primary.length > 1 ? primary[1].trim() : "desc";
            
            System.out.println("Parsed sortField: " + sortField);
            System.out.println("Parsed sortDirection: " + sortDirection);
            System.out.println("Direction after equalsIgnoreCase check: " + ("asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC"));
        }
        System.out.println("=== SORT DEBUG END ===");
        System.out.println();

        PagedTransactionResponse response = transactionService.getTransactionsPageable(
            page, size, sortField, sortDirection, sortParams,
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

