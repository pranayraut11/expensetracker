package com.example.expensetracker.dto;

import com.example.expensetracker.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSaveResult {
    private List<Transaction> savedTransactions = new ArrayList<>();
    private List<String> duplicateTransactions = new ArrayList<>();

    public int getRowsProcessed() {
        return savedTransactions.size() + duplicateTransactions.size();
    }

    public int getRowsSaved() {
        return savedTransactions.size();
    }

    public int getDuplicates() {
        return duplicateTransactions.size();
    }
}

