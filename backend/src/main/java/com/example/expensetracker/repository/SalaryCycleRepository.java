package com.example.expensetracker.repository;

import com.example.expensetracker.model.SalaryCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryCycleRepository extends JpaRepository<SalaryCycle, Long> {

    /**
     * Find all salary cycles ordered by start date descending (latest first)
     */
    List<SalaryCycle> findAllByOrderByStartDateDesc();

    /**
     * Find salary cycle by transaction ID
     */
    Optional<SalaryCycle> findBySalaryTransactionId(Long transactionId);

    /**
     * Find the next salary cycle after a given date
     */
    @Query("SELECT sc FROM SalaryCycle sc WHERE sc.startDate > :afterDate ORDER BY sc.startDate ASC LIMIT 1")
    Optional<SalaryCycle> findNextCycleAfter(@Param("afterDate") LocalDate afterDate);

    /**
     * Find salary cycle by date range
     */
    @Query("SELECT sc FROM SalaryCycle sc WHERE sc.startDate = :startDate AND sc.endDate = :endDate")
    Optional<SalaryCycle> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Get all salary cycles within a year
     */
    @Query("SELECT sc FROM SalaryCycle sc WHERE YEAR(sc.startDate) = :year ORDER BY sc.startDate ASC")
    List<SalaryCycle> findByYear(@Param("year") Integer year);
}

