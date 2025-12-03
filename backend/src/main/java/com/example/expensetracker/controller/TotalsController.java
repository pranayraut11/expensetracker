package com.example.expensetracker.controller;

import com.example.expensetracker.dto.TotalsDto;
import com.example.expensetracker.service.TotalsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/analytics")
public class TotalsController {

    private static final Logger logger = LoggerFactory.getLogger(TotalsController.class);

    private final TotalsService totalsService;

    public TotalsController(TotalsService totalsService) {
        this.totalsService = totalsService;
    }

    /**
     * Get total credit and debit amounts with optional filters
     *
     * IMPORTANT: This API automatically excludes credit card payments from calculations
     * but INCLUDES credit card transactions uploaded via CC statement
     *
     * Query Parameters:
     * - from: Start date (optional, format: yyyy-MM-dd)
     * - to: End date (optional, format: yyyy-MM-dd)
     * - category: Category filter (optional)
     * - search: Description search term (optional, case-insensitive partial match)
     *
     * If from/to are null → returns totals for ALL time
     * If category is null → returns totals for ALL categories
     * If search is null → no description filtering
     *
     * Examples:
     * GET /api/analytics/totals
     * GET /api/analytics/totals?from=2025-01-01&to=2025-12-31
     * GET /api/analytics/totals?category=Food
     * GET /api/analytics/totals?search=swiggy
     * GET /api/analytics/totals?from=2025-01-01&to=2025-12-31&category=Food&search=swiggy
     *
     * @param from Start date (optional)
     * @param to End date (optional)
     * @param category Category filter (optional)
     * @param search Description search term (optional)
     * @return TotalsDto with totalCredit and totalDebit
     */
    @GetMapping("/totals")
    public ResponseEntity<TotalsDto> getTotals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        logger.info("GET /api/analytics/totals - from: {}, to: {}, category: {}, search: {}", from, to, category, search);

        TotalsDto totals = totalsService.computeTotals(from, to, category, search);

        return ResponseEntity.ok(totals);
    }
}

