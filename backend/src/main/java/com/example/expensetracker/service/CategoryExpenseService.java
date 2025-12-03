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
}

