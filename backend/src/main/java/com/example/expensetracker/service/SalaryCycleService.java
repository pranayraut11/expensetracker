package com.example.expensetracker.service;

import com.example.expensetracker.dto.SalaryCycleDto;
import com.example.expensetracker.dto.SalaryCycleTotalsDto;
import com.example.expensetracker.model.SalaryCycle;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.repository.SalaryCycleRepository;
import com.example.expensetracker.repository.TransactionRepository;
import com.example.expensetracker.util.SalaryTransactionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class SalaryCycleService {

    private static final Logger logger = LoggerFactory.getLogger(SalaryCycleService.class);

    private final SalaryCycleRepository salaryCycleRepository;
    private final TransactionRepository transactionRepository;
    private final SalaryTransactionDetector salaryDetector;

    public SalaryCycleService(SalaryCycleRepository salaryCycleRepository,
                              TransactionRepository transactionRepository,
                              SalaryTransactionDetector salaryDetector) {
        this.salaryCycleRepository = salaryCycleRepository;
        this.transactionRepository = transactionRepository;
        this.salaryDetector = salaryDetector;
    }

    /**
     * Detect and create salary cycles from existing transactions
     * This should be called after transactions are imported
     */
    @Transactional
    public void detectAndCreateSalaryCycles() {
        logger.info("Starting salary cycle detection...");

        // Get all CREDIT transactions categorized as Income
        List<Transaction> allTransactions = transactionRepository.findAllIncome();

        // Filter for salary transactions
        List<Transaction> salaryTransactions = allTransactions.stream()
            .filter(salaryDetector::isSalaryTransaction)
            .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
            .toList();

        logger.info("Found {} salary transactions", salaryTransactions.size());

        // Create salary cycles
        for (int i = 0; i < salaryTransactions.size(); i++) {
            Transaction currentSalary = salaryTransactions.get(i);

            // Check if cycle already exists for this transaction
            Optional<SalaryCycle> existingCycle = salaryCycleRepository
                .findBySalaryTransactionId(currentSalary.getId());

            if (existingCycle.isPresent()) {
                logger.debug("Salary cycle already exists for transaction {}", currentSalary.getId());
                continue;
            }

            LocalDate startDate = currentSalary.getDate();
            LocalDate endDate;

            // Determine end date
            if (i < salaryTransactions.size() - 1) {
                // Next salary exists, end date = next salary date - 1 day
                Transaction nextSalary = salaryTransactions.get(i + 1);
                endDate = nextSalary.getDate().minusDays(1);
            } else {
                // This is the latest salary, end date = current date
                endDate = LocalDate.now();
            }

            // Create and save salary cycle
            SalaryCycle cycle = new SalaryCycle();
            cycle.setStartDate(startDate);
            cycle.setEndDate(endDate);
            cycle.setSalaryAmount(currentSalary.getAmount());
            cycle.setSalaryTransactionId(currentSalary.getId());

            salaryCycleRepository.save(cycle);
            logger.info("Created salary cycle: {} to {} (Salary: {})",
                startDate, endDate, currentSalary.getAmount());
        }

        logger.info("Salary cycle detection completed");
    }

    /**
     * Recalculate all salary cycles (used for manual recalculation)
     * Returns the count of created cycles
     */
    @Transactional
    public long recalculateAllCycles() {
        logger.info("Recalculating all salary cycles...");

        // Get all CREDIT transactions categorized as Income
        List<Transaction> allTransactions = transactionRepository.findAllIncome();

        // Filter for salary transactions
        List<Transaction> salaryTransactions = allTransactions.stream()
            .filter(salaryDetector::isSalaryTransaction)
            .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
            .toList();

        logger.info("Found {} salary transactions for recalculation", salaryTransactions.size());

        long createdCount = 0;

        // Create salary cycles
        for (int i = 0; i < salaryTransactions.size(); i++) {
            Transaction currentSalary = salaryTransactions.get(i);

            LocalDate startDate = currentSalary.getDate();
            LocalDate endDate;

            // Determine end date
            if (i < salaryTransactions.size() - 1) {
                // Next salary exists, end date = next salary date - 1 day
                Transaction nextSalary = salaryTransactions.get(i + 1);
                endDate = nextSalary.getDate().minusDays(1);
            } else {
                // This is the latest salary, end date = current date
                endDate = LocalDate.now();
            }

            // Create and save salary cycle
            SalaryCycle cycle = new SalaryCycle();
            cycle.setStartDate(startDate);
            cycle.setEndDate(endDate);
            cycle.setSalaryAmount(currentSalary.getAmount());
            cycle.setSalaryTransactionId(currentSalary.getId());

            salaryCycleRepository.save(cycle);
            createdCount++;

            logger.info("Created salary cycle: {} to {} (Salary: {})",
                startDate, endDate, currentSalary.getAmount());
        }

        logger.info("Salary cycle recalculation completed: {} cycles created", createdCount);
        return createdCount;
    }

    /**
     * Get all salary cycles
     */
    public List<SalaryCycleDto> getAllSalaryCycles() {
        List<SalaryCycle> cycles = salaryCycleRepository.findAllByOrderByStartDateDesc();
        List<SalaryCycleDto> dtos = new ArrayList<>();

        for (SalaryCycle cycle : cycles) {
            dtos.add(convertToDto(cycle));
        }

        return dtos;
    }

    /**
     * Get salary cycle by ID
     */
    public Optional<SalaryCycleDto> getSalaryCycleById(Long cycleId) {
        return salaryCycleRepository.findById(cycleId)
            .map(this::convertToDto);
    }

    /**
     * Calculate totals for a specific salary cycle
     */
    public SalaryCycleTotalsDto calculateSalaryCycleTotals(Long cycleId) {
        Optional<SalaryCycle> cycleOpt = salaryCycleRepository.findById(cycleId);

        if (cycleOpt.isEmpty()) {
            throw new IllegalArgumentException("Salary cycle not found: " + cycleId);
        }

        SalaryCycle cycle = cycleOpt.get();
        LocalDate startDate = cycle.getStartDate();
        LocalDate endDate = cycle.getEndDate();

        logger.info("Calculating totals for salary cycle {} ({} to {})",
            cycleId, startDate, endDate);

        // Calculate total credit (excludes CC payments, includes salary)
        Double totalCredit = transactionRepository.calculateTotalCredit(
            true, startDate, endDate, null, null);

        // Calculate total debit (includes CC transactions)
        Double totalDebit = transactionRepository.calculateTotalDebit(
            true, startDate, endDate, null, null);

        // Ensure non-null values
        totalCredit = totalCredit != null ? totalCredit : 0.0;
        totalDebit = totalDebit != null ? totalDebit : 0.0;

        // Calculate net savings
        Double netSavings = totalCredit - totalDebit;

        logger.info("Salary cycle totals - Credit: {}, Debit: {}, Savings: {}",
            totalCredit, totalDebit, netSavings);

        return new SalaryCycleTotalsDto(totalCredit, totalDebit, netSavings, cycle.getSalaryAmount());
    }

    /**
     * Refresh salary cycles (re-detect and update)
     */
    @Transactional
    public void refreshSalaryCycles() {
        logger.info("Refreshing salary cycles...");

        // Delete all existing cycles
        salaryCycleRepository.deleteAll();

        // Re-create cycles
        detectAndCreateSalaryCycles();

        logger.info("Salary cycles refreshed");
    }

    /**
     * Update end date of last salary cycle to current date
     * Should be called periodically to keep the last cycle up-to-date
     */
    @Transactional
    public void updateLastSalaryCycleEndDate() {
        List<SalaryCycle> cycles = salaryCycleRepository.findAllByOrderByStartDateDesc();

        if (!cycles.isEmpty()) {
            SalaryCycle lastCycle = cycles.get(0);
            LocalDate newEndDate = LocalDate.now();

            if (!lastCycle.getEndDate().equals(newEndDate)) {
                lastCycle.setEndDate(newEndDate);
                salaryCycleRepository.save(lastCycle);
                logger.info("Updated last salary cycle end date to {}", newEndDate);
            }
        }
    }

    /**
     * Convert SalaryCycle entity to DTO
     */
    private SalaryCycleDto convertToDto(SalaryCycle cycle) {
        String label = generateCycleLabel(cycle.getStartDate(), cycle.getEndDate());

        return new SalaryCycleDto(
            cycle.getId(),
            label,
            cycle.getStartDate(),
            cycle.getEndDate(),
            cycle.getSalaryAmount()
        );
    }

    /**
     * Generate a user-friendly label for salary cycle
     * Example: "Jan Salary Cycle (5 Jan – 4 Feb)"
     */
    private String generateCycleLabel(LocalDate startDate, LocalDate endDate) {
        String startMonth = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int startDay = startDate.getDayOfMonth();

        String endMonth = endDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int endDay = endDate.getDayOfMonth();

        return String.format("%s Salary Cycle (%d %s – %d %s)",
            startMonth, startDay, startMonth, endDay, endMonth);
    }
}

