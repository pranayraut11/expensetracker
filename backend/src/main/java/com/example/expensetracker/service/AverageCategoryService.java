package com.example.expensetracker.service;

import com.example.expensetracker.dto.AverageCategoryDto;
import com.example.expensetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@Service
public class AverageCategoryService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public AverageCategoryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculate average monthly income and expense by category
     * @param year Year for analysis (null = current year)
     * @param months Number of months to analyze
     * @return List of categories with average data
     */
    public List<AverageCategoryDto> calculateAverageByCategory(Integer year, Integer months) {
        // Default to current year if not provided
        if (year == null) {
            year = Year.now().getValue();
        }

        // Validate months parameter
        if (months == null || months <= 0) {
            months = 12;
        }

        // Calculate date range
        LocalDate endDate = LocalDate.of(year, 12, 31);
        LocalDate startDate = endDate.minusMonths(months - 1).withDayOfMonth(1);

        // Fetch all transactions in the date range
        var transactions = transactionRepository.findByDateBetween(startDate, endDate);

        // Group by category and calculate totals
        Map<String, CategoryStats> categoryStatsMap = new HashMap<>();

        for (var transaction : transactions) {
            String category = transaction.getCategory();
            if (category == null || category.isEmpty()) {
                category = "Uncategorized";
            }

            // Skip Credit Card Payment category
            if ("Credit Card Payment".equals(category)) {
                continue;
            }

            categoryStatsMap.putIfAbsent(category, new CategoryStats());
            CategoryStats stats = categoryStatsMap.get(category);

            if ("CREDIT".equals(transaction.getType())) {
                stats.totalIncome += transaction.getAmount();
            } else if ("DEBIT".equals(transaction.getType())) {
                stats.totalExpense += transaction.getAmount();
            }
        }

        // Convert to DTOs and calculate averages
        List<AverageCategoryDto> result = new ArrayList<>();

        for (Map.Entry<String, CategoryStats> entry : categoryStatsMap.entrySet()) {
            String category = entry.getKey();
            CategoryStats stats = entry.getValue();

            double avgIncome = stats.totalIncome / months;
            double avgExpense = stats.totalExpense / months;

            AverageCategoryDto dto = new AverageCategoryDto(
                category,
                stats.totalIncome,
                stats.totalExpense,
                avgIncome,
                avgExpense,
                months
            );

            result.add(dto);
        }

        // Sort by total expense (descending) for expenses, and by total income (descending) for income
        result.sort((a, b) -> {
            double aTotal = a.getTotalExpense() + a.getTotalIncome();
            double bTotal = b.getTotalExpense() + b.getTotalIncome();
            return Double.compare(bTotal, aTotal);
        });

        return result;
    }

    // Inner class to track statistics
    private static class CategoryStats {
        double totalIncome = 0.0;
        double totalExpense = 0.0;
    }
}
