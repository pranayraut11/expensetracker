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
@CrossOrigin(origins = "http://localhost:5173")
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
     * Get category-wise expenses for a specific month
     * GET /api/analytics/category-expenses?year=2024&month=2
     */
    @GetMapping("/category-expenses")
    public ResponseEntity<?> getCategoryExpenses(
            @RequestParam int year,
            @RequestParam int month
    ) {
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body("Month must be between 1 and 12");
        }

        List<CategoryExpenseDto> categoryExpenses = categoryExpenseService.getCategoryExpenses(year, month);
        return ResponseEntity.ok(categoryExpenses);
    }
}

