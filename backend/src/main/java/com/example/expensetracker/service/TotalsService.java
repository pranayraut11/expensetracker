package com.example.expensetracker.service;

import com.example.expensetracker.dto.TotalsDto;
import com.example.expensetracker.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TotalsService {

    private static final Logger logger = LoggerFactory.getLogger(TotalsService.class);

    private final TransactionRepository transactionRepository;

    public TotalsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Compute total credit and debit amounts with optional filters
     *
     * IMPORTANT BUSINESS RULES:
     * 1. Credit card transactions (isCreditCardTransaction = true) ARE included
     * 2. Credit card payments (isCreditCardPayment = true) are EXCLUDED
     * 3. All calculations use includeInTotals = true filter
     *
     * @param from Start date (optional, null = no date filter)
     * @param to End date (optional, null = no date filter)
     * @param category Category filter (optional, null = all categories)
     * @param search Description search term (optional, null = no search filter)
     * @return TotalsDto with totalCredit and totalDebit
     */
    public TotalsDto computeTotals(LocalDate from, LocalDate to, String category, String search) {
        logger.info("Computing totals: from={}, to={}, category={}, search={}", from, to, category, search);

        // Calculate total credit (includes CC transactions, excludes CC payments)
        Double totalCredit = transactionRepository.calculateTotalCredit(true, from, to, category, search);

        // Calculate total debit (includes CC transactions, excludes CC payments)
        Double totalDebit = transactionRepository.calculateTotalDebit(true, from, to, category, search);

        // Ensure non-null values
        totalCredit = totalCredit != null ? totalCredit : 0.0;
        totalDebit = totalDebit != null ? totalDebit : 0.0;

        logger.info("Computed totals: totalCredit={}, totalDebit={}", totalCredit, totalDebit);

        return new TotalsDto(totalCredit, totalDebit);
    }
}

