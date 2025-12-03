package com.example.expensetracker.service;

import com.example.expensetracker.dto.IncomeExpenseDailyDto;
import com.example.expensetracker.dto.IncomeExpenseMonthlyDto;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncomeExpenseTrendService {

    private final TransactionRepository transactionRepository;

    public IncomeExpenseTrendService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Get monthly trend for entire year
     * @param year The year to fetch data for
     * @return List of monthly income/expense data
     */
    public List<IncomeExpenseMonthlyDto> getMonthlyTrend(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);

        if (transactions.isEmpty()) {
            return new ArrayList<>();
        }

        // Group transactions by month (Year-Month format: "2024-01")
        Map<String, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> {
                    YearMonth yearMonth = YearMonth.from(transaction.getDate());
                    return yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                }));

        // Sort months chronologically
        List<String> sortedMonths = transactionsByMonth.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        // Calculate income and expenses for each month
        List<IncomeExpenseMonthlyDto> result = new ArrayList<>();
        for (String month : sortedMonths) {
            List<Transaction> monthTransactions = transactionsByMonth.get(month);

            double monthlyIncome = monthTransactions.stream()
                    .filter(t -> "CREDIT".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double monthlyExpenses = monthTransactions.stream()
                    .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            result.add(new IncomeExpenseMonthlyDto(month, monthlyIncome, monthlyExpenses));
        }

        return result;
    }

    /**
     * Get daily trend for specific month
     * @param year The year
     * @param month The month (1-12)
     * @return List of daily income/expense data
     */
    public List<IncomeExpenseDailyDto> getDailyTrend(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);

        if (transactions.isEmpty()) {
            return new ArrayList<>();
        }

        // Group transactions by date
        Map<LocalDate, List<Transaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getDate));

        // Get all dates in the month (to show even days with no transactions)
        List<LocalDate> allDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // Calculate income and expenses for each day
        List<IncomeExpenseDailyDto> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate date : allDates) {
            List<Transaction> dayTransactions = transactionsByDate.getOrDefault(date, new ArrayList<>());

            double dailyIncome = dayTransactions.stream()
                    .filter(t -> "CREDIT".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            double dailyExpenses = dayTransactions.stream()
                    .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            result.add(new IncomeExpenseDailyDto(date.format(formatter), dailyIncome, dailyExpenses));
        }

        return result;
    }
}

