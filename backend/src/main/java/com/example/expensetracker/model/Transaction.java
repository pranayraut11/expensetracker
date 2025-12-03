package com.example.expensetracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_hash", columnList = "transactionHash", unique = true),
    @Index(name = "idx_fingerprint_hash", columnList = "fingerprintHash", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(length = 100)
    private String refNo; // Reference number from bank statement

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 20)
    private String type; // DEBIT or CREDIT

    private Double balance;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 64, unique = true)
    private String transactionHash; // SHA-256 hash for duplicate detection

    // Credit Card Related Fields
    @Column(nullable = false)
    private Boolean isCreditCardTransaction = false; // True if from CC statement

    @Column(nullable = false)
    private Boolean isCreditCardPayment = false; // True if bank payment to CC

    @Column(nullable = false)
    private Boolean includeInTotals = true; // Derived: exclude CC payments from totals

    @Column(length = 100)
    private Long creditCardAccountId; // Optional: link to CC account

    @Column(length = 64, unique = true)
    private String fingerprintHash; // Alternative hash for CC transactions

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

