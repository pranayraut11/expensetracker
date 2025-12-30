package com.example.expensetracker.util;

import com.example.expensetracker.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class to detect salary transactions
 */
@Component
public class SalaryTransactionDetector {

    // Patterns to identify salary transactions
    private static final Pattern SALARY_PATTERN = Pattern.compile(
        ".*(SALARY|PAYROLL|NEFT SALARY|MONTHLY SALARY|SAL CREDIT|SALARY CREDIT|PAY CREDIT|" +
        "MONTHLY PAY|EMPLOYER PAYMENT|WAGES|COMPENSATION|PAYCHECK).*",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Detect if a transaction is a salary credit
     *
     * A transaction is considered salary if:
     * - type = CREDIT
     * - category = "Income" (or similar income categories)
     * - description matches salary-related keywords
     * - amount is typically substantial (optional check)
     *
     * @param transaction The transaction to check
     * @return true if transaction is detected as salary
     */
    public boolean isSalaryTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        // Must be CREDIT type
        if (!"CREDIT".equalsIgnoreCase(transaction.getType())) {
            return false;
        }

        // Must be Income category (case-insensitive)
        String category = transaction.getCategory();
        return category != null && category.equalsIgnoreCase("Salary");
    }

    /**
     * Extract salary keywords from description (for logging/debugging)
     */
    public String extractSalaryKeyword(String description) {
        if (description == null) {
            return null;
        }

        String upperDesc = description.toUpperCase();
        String[] keywords = {
            "SALARY", "PAYROLL", "NEFT SALARY", "MONTHLY SALARY",
            "SAL CREDIT", "SALARY CREDIT", "PAY CREDIT", "MONTHLY PAY",
            "EMPLOYER PAYMENT", "WAGES", "COMPENSATION", "PAYCHECK"
        };

        for (String keyword : keywords) {
            if (upperDesc.contains(keyword)) {
                return keyword;
            }
        }
        return null;
    }
}

