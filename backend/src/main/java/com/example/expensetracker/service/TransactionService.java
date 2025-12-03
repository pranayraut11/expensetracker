package com.example.expensetracker.service;

import com.example.expensetracker.dto.PagedTransactionResponse;
import com.example.expensetracker.dto.TagSuggestionDto;
import com.example.expensetracker.dto.TransactionDto;
import com.example.expensetracker.dto.TransactionSaveResult;
import com.example.expensetracker.model.Tag;
import com.example.expensetracker.model.Transaction;
import com.example.expensetracker.repository.TagRepository;
import com.example.expensetracker.repository.TransactionRepository;
import com.example.expensetracker.specification.TransactionSpecification;
import com.example.expensetracker.util.DescriptionCleaner;
import com.example.expensetracker.util.MerchantNormalizer;
import com.example.expensetracker.util.TransactionHashUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final DynamicDroolsService dynamicDroolsService;
    private final MerchantNormalizer merchantNormalizer;
    private final TagExtractionService tagExtractorService;
    private final TagRepository tagRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              DynamicDroolsService dynamicDroolsService,
                              MerchantNormalizer merchantNormalizer,
                              TagExtractionService tagExtractorService,
                              TagRepository tagRepository) {
        this.transactionRepository = transactionRepository;
        this.dynamicDroolsService = dynamicDroolsService;
        this.merchantNormalizer = merchantNormalizer;
        this.tagExtractorService = tagExtractorService;
        this.tagRepository = tagRepository;
    }

    /**
     * Save a list of transactions with duplicate detection
     * Returns saved transactions and list of duplicates
     * This method is non-transactional and delegates to transactional method per item
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public TransactionSaveResult saveAllWithDuplicateCheck(List<Transaction> transactions) {
        TransactionSaveResult result = new TransactionSaveResult();

        for (Transaction transaction : transactions) {
            // Generate transaction hash if not already set (for bank statements)
            if (transaction.getTransactionHash() == null || transaction.getTransactionHash().isEmpty()) {
                String hash = TransactionHashUtil.generateHash(
                    transaction.getDescription(),
                    transaction.getRefNo(),
                    transaction.getDate(),
                    transaction.getAmount(),
                    transaction.getType()
                );
                transaction.setTransactionHash(hash);
            }

            // Ensure credit card fields have default values if not set
            if (transaction.getIsCreditCardTransaction() == null) {
                transaction.setIsCreditCardTransaction(false);
            }
            if (transaction.getIsCreditCardPayment() == null) {
                transaction.setIsCreditCardPayment(false);
            }
            if (transaction.getIncludeInTotals() == null) {
                transaction.setIncludeInTotals(true);
            }

            // Try to save in a separate transaction to isolate exceptions
            try {
                Transaction saved = saveTransactionIndividually(transaction);
                if (saved != null) {
                    result.getSavedTransactions().add(saved);
                }
            } catch (Exception e) {
                // Check if it's a duplicate or other error
                if (e instanceof DataIntegrityViolationException ||
                    e.getCause() instanceof DataIntegrityViolationException ||
                    (e.getMessage() != null && e.getMessage().contains("constraint"))) {
                    // Duplicate detected - hash constraint violation
                    String duplicateInfo = String.format(
                        "Date: %s, Description: %s, Amount: %.2f, Type: %s",
                        transaction.getDate(),
                        transaction.getDescription().length() > 50
                            ? transaction.getDescription().substring(0, 50) + "..."
                            : transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getType()
                    );
                    result.getDuplicateTransactions().add(duplicateInfo);
                } else {
                    // Other errors - log and skip
                    String errorInfo = String.format(
                        "Error: %s, Date: %s, Amount: %.2f",
                        e.getMessage(),
                        transaction.getDate(),
                        transaction.getAmount()
                    );
                    result.getDuplicateTransactions().add(errorInfo);
                }
            }
        }

        // Extract and save tags from successfully saved transactions
        if (!result.getSavedTransactions().isEmpty()) {
            saveTags(result.getSavedTransactions());
        }

        return result;
    }

    /**
     * Save a single transaction in its own transaction
     * Each call runs in a new transaction, isolated from others
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction saveTransactionIndividually(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Save a list of transactions
     */
    @Transactional
    public List<Transaction> saveAll(List<Transaction> transactions) {
        // Generate hash for each transaction
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionHash() == null || transaction.getTransactionHash().isEmpty()) {
                String hash = TransactionHashUtil.generateHash(
                    transaction.getDescription(),
                    transaction.getRefNo(),
                    transaction.getDate(),
                    transaction.getAmount(),
                    transaction.getType()
                );
                transaction.setTransactionHash(hash);
            }

            // Ensure default values
            if (transaction.getIsCreditCardTransaction() == null) {
                transaction.setIsCreditCardTransaction(false);
            }
            if (transaction.getIsCreditCardPayment() == null) {
                transaction.setIsCreditCardPayment(false);
            }
            if (transaction.getIncludeInTotals() == null) {
                transaction.setIncludeInTotals(true);
            }
        }

        List<Transaction> saved = transactionRepository.saveAll(transactions);
        saveTags(saved);
        return saved;
    }

    /**
     * Extract and save tags from transactions
     */
    private void saveTags(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            String description = transaction.getDescription();
            if (description != null && !description.isEmpty()) {
                String normalized = merchantNormalizer.normalize(description);
                tagExtractorService.extractTags(normalized);
            }
        }
    }

    /**
     * Get transactions with pagination, filtering, and sorting
     */
    @Transactional(readOnly = true)
    public PagedTransactionResponse getTransactionsPageable(
            int page,
            int size,
            String sortField,
            String sortDirection,
            String search,
            String category,
            String type,
            Boolean isCreditCard,
            LocalDate fromDate,
            LocalDate toDate) {

        // Build sort
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build specification with filters
        Specification<Transaction> spec = TransactionSpecification.filterTransactions(
            search, category, type, isCreditCard, fromDate, toDate
        );

        // Execute query
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        // Convert to DTOs
        List<TransactionDto> dtos = transactionPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Build response
        return new PagedTransactionResponse(
            dtos,
            transactionPage.getTotalElements(),
            transactionPage.getTotalPages(),
            transactionPage.getNumber(),
            transactionPage.getSize()
        );
    }

    /**
     * Get summary with totals and category breakdown
     */
    @Transactional(readOnly = true)
    public com.example.expensetracker.dto.SummaryDto getSummary(String category, LocalDate fromDate, LocalDate toDate) {
        // Calculate totals using repository queries
        Double totalCredit = transactionRepository.calculateTotalCredit(true, fromDate, toDate, category, null);
        Double totalDebit = transactionRepository.calculateTotalDebit(true, fromDate, toDate, category, null);

        // Get transaction count
        Long transactionCount = transactionRepository.countTransactions(category, null, fromDate, toDate);

        // Get opening and closing balance
        Double openingBalance = transactionRepository.getOpeningBalance(category, fromDate, toDate);
        Double closingBalance = transactionRepository.getClosingBalance(category, fromDate, toDate);

        // Calculate category breakdown by querying transactions
        List<Transaction> transactions;
        if (category != null && fromDate != null && toDate != null) {
            transactions = transactionRepository.findByDateBetween(fromDate, toDate).stream()
                    .filter(t -> t.getCategory().equals(category) && t.getIncludeInTotals())
                    .toList();
        } else if (category != null) {
            transactions = transactionRepository.findByCategory(category).stream()
                    .filter(Transaction::getIncludeInTotals)
                    .toList();
        } else if (fromDate != null && toDate != null) {
            transactions = transactionRepository.findByDateBetween(fromDate, toDate).stream()
                    .filter(Transaction::getIncludeInTotals)
                    .toList();
        } else {
            transactions = transactionRepository.findAll().stream()
                    .filter(Transaction::getIncludeInTotals)
                    .toList();
        }

        // Group by category and sum amounts for DEBIT transactions
        Map<String, Double> categoryBreakdown = transactions.stream()
                .filter(t -> "DEBIT".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        return new com.example.expensetracker.dto.SummaryDto(
                totalCredit != null ? totalCredit : 0.0,
                totalDebit != null ? totalDebit : 0.0,
                categoryBreakdown,
                transactionCount != null ? transactionCount : 0L,
                openingBalance,
                closingBalance
        );
    }

    /**
     * Update transaction category
     */
    @Transactional
    public TransactionDto updateCategory(Long id, String category) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        transaction.setCategory(category);
        Transaction saved = transactionRepository.save(transaction);
        return convertToDto(saved);
    }

    /**
     * Get all transactions
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(TransactionDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Search transactions by description
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> searchTransactions(String searchText, String category, LocalDate fromDate, LocalDate toDate) {
        List<Transaction> transactions;

        if (category != null && fromDate != null && toDate != null) {
            transactions = transactionRepository.searchByDescriptionAndCategoryAndDateBetween(
                    searchText.trim(), category, fromDate, toDate);
        } else if (category != null) {
            transactions = transactionRepository.searchByDescriptionAndCategory(searchText.trim(), category);
        } else if (fromDate != null && toDate != null) {
            transactions = transactionRepository.searchByDescriptionAndDateBetween(
                    searchText.trim(), fromDate, toDate);
        } else {
            transactions = transactionRepository.searchByDescription(searchText.trim());
        }

        return transactions.stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(TransactionDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get top tags (normalized merchant names) for rule creation
     */
    @Transactional(readOnly = true)
    public List<TagSuggestionDto> getTopTags(int limit) {
        List<Tag> all = tagRepository.findAll();
        List<TagSuggestionDto> suggestions = new ArrayList<>();

        for (Tag t : all) {
            suggestions.add(TagSuggestionDto.builder()
                    .tag(t.getTagName())
                    .count(t.getUsageCount())
                    .build());
        }

        return suggestions.stream()
                .sorted(Comparator.comparingLong(TagSuggestionDto::getCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Search tags by partial name match
     */
    @Transactional(readOnly = true)
    public List<TagSuggestionDto> searchTags(String searchTerm, Integer limit) {
        List<Tag> matchingTags;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            matchingTags = tagRepository.findAll();
        } else {
            matchingTags = tagRepository.searchByTagName(searchTerm.trim());
        }

        List<TagSuggestionDto> suggestions = matchingTags.stream()
                .map(t -> TagSuggestionDto.builder()
                        .tag(t.getTagName())
                        .count(t.getUsageCount())
                        .build())
                .sorted(Comparator.comparingLong(TagSuggestionDto::getCount).reversed())
                .collect(Collectors.toList());

        if (limit != null && limit > 0) {
            return suggestions.stream().limit(limit).collect(Collectors.toList());
        }

        return suggestions;
    }

    /**
     * Convert Transaction entity to DTO
     */
    private TransactionDto convertToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .refNo(transaction.getRefNo())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .balance(transaction.getBalance())
                .category(transaction.getCategory())
                .isCreditCardTransaction(transaction.getIsCreditCardTransaction())
                .isCreditCardPayment(transaction.getIsCreditCardPayment())
                .includeInTotals(transaction.getIncludeInTotals())
                .transactionHash(transaction.getTransactionHash())
                .build();
    }

    /**
     * Recategorize all transactions using current rules
     */
    @Transactional
    public int recategorizeAll() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        int count = 0;

        for (Transaction transaction : allTransactions) {
            count = count + dynamicDroolsService.applyRules(transaction);
        }

        logger.info("Recategorized {} transactions", count);
        return count;
    }
}

