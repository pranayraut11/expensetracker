package com.example.expensetracker.specification;

import com.example.expensetracker.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * JPA Specification for building dynamic queries for Transaction entity
 */
public class TransactionSpecification {

    /**
     * Filter by description (case-insensitive partial match)
     */
    public static Specification<Transaction> descriptionContains(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")),
                "%" + search.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filter by exact category name
     */
    public static Specification<Transaction> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    /**
     * Filter by transaction type (CREDIT or DEBIT)
     */
    public static Specification<Transaction> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null || type.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    /**
     * Filter by credit card transaction flag
     */
    public static Specification<Transaction> isCreditCardTransaction(Boolean isCreditCard) {
        return (root, query, criteriaBuilder) -> {
            if (isCreditCard == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isCreditCardTransaction"), isCreditCard);
        };
    }

    /**
     * Filter by date range
     * Only applies if BOTH fromDate and toDate are provided
     */
    public static Specification<Transaction> dateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null || toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get("date"), fromDate, toDate);
        };
    }

    /**
     * ALWAYS exclude transactions with category = "Credit Card Payment"
     */
    public static Specification<Transaction> excludeCreditCardPayment() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get("category"), "Credit Card Payment");
    }

    /**
     * Combine all specifications
     */
    public static Specification<Transaction> filterTransactions(
            String search,
            String category,
            String type,
            Boolean isCreditCard,
            LocalDate fromDate,
            LocalDate toDate) {

        return Specification.where(excludeCreditCardPayment())
                .and(descriptionContains(search))
                .and(hasCategory(category))
                .and(hasType(type))
                .and(isCreditCardTransaction(isCreditCard))
                .and(dateBetween(fromDate, toDate));
    }
}

