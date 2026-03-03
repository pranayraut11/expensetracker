package com.example.expensetracker.controller;

import com.example.expensetracker.dto.CategoryExpenseDto;
import com.example.expensetracker.dto.IncomeExpenseDailyDto;
import com.example.expensetracker.dto.IncomeExpenseMonthlyDto;
import com.example.expensetracker.service.CategoryExpenseService;
import com.example.expensetracker.service.IncomeExpenseTrendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class IncomeExpenseTrendController {

    private final IncomeExpenseTrendService trendService;
    private final CategoryExpenseService categoryExpenseService;

    public IncomeExpenseTrendController(IncomeExpenseTrendService trendService,
                                       CategoryExpenseService categoryExpenseService) {
        this.trendService = trendService;
        this.categoryExpenseService = categoryExpenseService;
    }

    /**
     * Get income vs expense trend
     * If month is provided: returns daily trend for that month
     * If only year is provided: returns monthly trend for the year
     *
     * GET /api/analytics/income-expense-trend?year=2024
     * GET /api/analytics/income-expense-trend?year=2024&month=2
     */
    @GetMapping("/income-expense-trend")
    public ResponseEntity<?> getIncomeExpenseTrend(
            @RequestParam int year,
            @RequestParam(required = false) Integer month
    ) {
        if (month != null) {
            // Return daily trend for specific month
            if (month < 1 || month > 12) {
                return ResponseEntity.badRequest().body("Month must be between 1 and 12");
            }
            List<IncomeExpenseDailyDto> dailyTrend = trendService.getDailyTrend(year, month);
            return ResponseEntity.ok(dailyTrend);
        } else {
            // Return monthly trend for entire year
            List<IncomeExpenseMonthlyDto> monthlyTrend = trendService.getMonthlyTrend(year);
            return ResponseEntity.ok(monthlyTrend);
        }
    }

    /**
     * Get category-wise expenses for a specific month or entire year
     * GET /api/analytics/category-expenses?year=2024&month=2
     * GET /api/analytics/category-expenses?year=2024&month=all
     */
    @GetMapping("/category-expenses")
    public ResponseEntity<?> getCategoryExpenses(
            @RequestParam int year,
            @RequestParam(required = false) String month
    ) {
        // If month is "all", get expenses for entire year
        if ("all".equalsIgnoreCase(month)) {
            List<CategoryExpenseDto> categoryExpenses = categoryExpenseService.getCategoryExpensesForYear(year);
            return ResponseEntity.ok(categoryExpenses);
        }

        // Otherwise, parse month as integer
        try {
            int monthInt = Integer.parseInt(month);
            if (monthInt < 1 || monthInt > 12) {
                return ResponseEntity.badRequest().body("Month must be between 1 and 12");
            }
            List<CategoryExpenseDto> categoryExpenses = categoryExpenseService.getCategoryExpenses(year, monthInt);
            return ResponseEntity.ok(categoryExpenses);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Month must be a number between 1-12 or 'all'");
        }
    }

    /**
     * Get category-wise expenses for a date range with optional filters
     * GET /api/analytics/category-expenses-range?fromDate=2024-01-01&toDate=2024-12-31&search=&category=
     */
    @GetMapping("/category-expenses-range")
    public ResponseEntity<?> getCategoryExpensesByDateRange(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        try {
            List<CategoryExpenseDto> categoryExpenses = categoryExpenseService.getCategoryExpensesByDateRange(
                fromDate, toDate, search, category
            );
            return ResponseEntity.ok(categoryExpenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching category expenses: " + e.getMessage());
        }
    }
}
