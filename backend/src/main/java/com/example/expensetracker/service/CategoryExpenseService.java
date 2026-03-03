package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategoryExpenseDto;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryExpenseService {

    private final TransactionRepository transactionRepository;

    public CategoryExpenseService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Get category-wise expenses for a specific month
     * @param year The year
     * @param month The month (1-12)
     * @return List of category expenses sorted by total (descending)
     */
    public List<CategoryExpenseDto> getCategoryExpenses(int year, int month) {
        // Calculate start and end dates for the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        // Fetch all transactions for the month
        List<Transaction> transactions = transactionRepository.findByDateBetween(startOfMonth, endOfMonth);

        // Filter only DEBIT transactions (expenses)
        List<Transaction> expenses = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());

        // Group by category and sum amounts
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Convert to DTO list and sort by total (descending)
        return categoryTotals.entrySet().stream()
                .map(entry -> new CategoryExpenseDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategoryExpenseDto::getTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get category-wise expenses for an entire year
     * @param year The year
     * @return List of category expenses sorted by total (descending)
     */
    public List<CategoryExpenseDto> getCategoryExpensesForYear(int year) {
        // Calculate start and end dates for the year
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        // Fetch all transactions for the year
        List<Transaction> transactions = transactionRepository.findByDateBetween(startOfYear, endOfYear);

        // Filter only DEBIT transactions (expenses)
        List<Transaction> expenses = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());

        // Group by category and sum amounts
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Convert to DTO list and sort by total (descending)
        return categoryTotals.entrySet().stream()
                .map(entry -> new CategoryExpenseDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategoryExpenseDto::getTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get category-wise expenses for a date range with optional filters
     * @param fromDate Start date (YYYY-MM-DD format)
     * @param toDate End date (YYYY-MM-DD format)
     * @param search Optional search term to filter by description
     * @param category Optional category to filter by
     * @return List of category expenses sorted by total (descending)
     */
    public List<CategoryExpenseDto> getCategoryExpensesByDateRange(String fromDate, String toDate, String search, String category) {
        LocalDate startDate = fromDate != null && !fromDate.isEmpty() ? LocalDate.parse(fromDate) : null;
        LocalDate endDate = toDate != null && !toDate.isEmpty() ? LocalDate.parse(toDate) : null;

        // Fetch transactions
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByDateBetween(startDate, endDate);
        } else {
            transactions = transactionRepository.findAll();
        }

        // Filter transactions
        List<Transaction> filtered = transactions.stream()
                // Only DEBIT transactions (expenses)
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                // Exclude credit card payments
                .filter(t -> !("Credit Card Payment".equalsIgnoreCase(t.getCategory())))
                // Filter by search term if provided
                .filter(t -> search == null || search.isEmpty() ||
                           (t.getDescription() != null && t.getDescription().toLowerCase().contains(search.toLowerCase())))
                // Filter by category if provided
                .filter(t -> category == null || category.isEmpty() ||
                           (t.getCategory() != null && t.getCategory().equalsIgnoreCase(category)))
                .collect(Collectors.toList());

        // Group by category and sum amounts
        Map<String, Double> categoryTotals = filtered.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Convert to DTO list and sort by total (descending)
        return categoryTotals.entrySet().stream()
                .map(entry -> new CategoryExpenseDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategoryExpenseDto::getTotal).reversed())
                .collect(Collectors.toList());
    }
}
