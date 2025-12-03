package com.example.expensetracker.repository;

import com.example.expensetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByCategory(String category);

    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Transaction> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE t.type = 'CREDIT'")
    List<Transaction> findAllIncome();

    @Query("SELECT t FROM Transaction t WHERE t.type = 'DEBIT'")
    List<Transaction> findAllExpenses();

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Transaction> searchByDescription(@Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND t.category = :category")
    List<Transaction> searchByDescriptionAndCategory(@Param("searchTerm") String searchTerm,
                                                      @Param("category") String category);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> searchByDescriptionAndDateBetween(@Param("searchTerm") String searchTerm,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND t.category = :category AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> searchByDescriptionAndCategoryAndDateBetween(@Param("searchTerm") String searchTerm,
                                                                    @Param("category") String category,
                                                                    @Param("startDate") LocalDate startDate,
                                                                    @Param("endDate") LocalDate endDate);

    /**
     * Calculate total CREDIT amount with filters
     * IMPORTANT: Only includes transactions where includeInTotals = true
     * This excludes credit card payments from bank statements
     */
    @Query("""
    SELECT COALESCE(SUM(t.amount), 0.0)
    FROM Transaction t
    WHERE t.type = 'CREDIT'
      AND t.includeInTotals = :includeInTotals
      AND t.isCreditCardTransaction = false
      AND (:category IS NULL OR t.category = :category)
      AND (:from IS NULL OR t.date >= :from)
      AND (:to IS NULL OR t.date <= :to)
      AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Double calculateTotalCredit(@Param("includeInTotals") Boolean includeInTotals,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to,
                                 @Param("category") String category,
                                 @Param("search") String search);

    /**
     * Calculate total DEBIT amount with filters
     * IMPORTANT: Only includes transactions where includeInTotals = true
     * This excludes credit card payments from bank statements
     */
    @Query("""
    SELECT COALESCE(SUM(t.amount), 0.0)
    FROM Transaction t
    WHERE t.type = 'DEBIT'
      AND t.includeInTotals = :includeInTotals
      AND (:category IS NULL OR t.category = :category)
      AND (:from IS NULL OR t.date >= :from)
      AND (:to IS NULL OR t.date <= :to)
      AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Double calculateTotalDebit(@Param("includeInTotals") Boolean includeInTotals,
                                @Param("from") LocalDate from,
                                @Param("to") LocalDate to,
                                @Param("category") String category,
                                @Param("search") String search);

    /**
     * Count transactions with filters
     */
    @Query("""
    SELECT COUNT(t)
    FROM Transaction t
    WHERE (:category IS NULL OR t.category = :category)
      AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:from IS NULL OR t.date >= :from)
      AND (:to IS NULL OR t.date <= :to)
    """)
    Long countTransactions(@Param("category") String category,
                           @Param("search") String search,
                           @Param("from") LocalDate from,
                           @Param("to") LocalDate to);

    /**
     * Get opening balance (balance of first transaction in the date range)
     * If no filters, gets the earliest transaction's balance
     */
    @Query("""
    SELECT t.balance
    FROM Transaction t
    WHERE (:category IS NULL OR t.category = :category)
      AND (:from IS NULL OR t.date >= :from)
      AND (:to IS NULL OR t.date <= :to)
    ORDER BY t.date ASC, t.id ASC
    LIMIT 1
    """)
    Double getOpeningBalance(@Param("category") String category,
                             @Param("from") LocalDate from,
                             @Param("to") LocalDate to);

    /**
     * Get closing balance (balance of last transaction in the date range)
     * If no filters, gets the latest transaction's balance
     */
    @Query("""
    SELECT t.balance
    FROM Transaction t
    WHERE (:category IS NULL OR t.category = :category)
      AND (:from IS NULL OR t.date >= :from)
      AND (:to IS NULL OR t.date <= :to)
    ORDER BY t.date DESC, t.id DESC
    LIMIT 1
    """)
    Double getClosingBalance(@Param("category") String category,
                             @Param("from") LocalDate from,
                             @Param("to") LocalDate to);
}

