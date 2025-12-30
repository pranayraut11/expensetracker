package com.example.expensetracker.service;

import com.example.expensetracker.repository.RuleDefinitionRepository;
import com.example.expensetracker.repository.TagRepository;
import com.example.expensetracker.repository.TransactionRepository;
import com.example.expensetracker.repository.SalaryCycleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);

    private final TransactionRepository transactionRepository;
    private final TagRepository tagRepository;
    private final RuleDefinitionRepository ruleDefinitionRepository;
    private final DynamicDroolsService dynamicDroolsService;
    private final SalaryCycleRepository salaryCycleRepository;
    private final SalaryCycleService salaryCycleService;

    @Autowired
    public SettingsService(TransactionRepository transactionRepository,
                          TagRepository tagRepository,
                          RuleDefinitionRepository ruleDefinitionRepository,
                          DynamicDroolsService dynamicDroolsService,
                          SalaryCycleRepository salaryCycleRepository,
                          SalaryCycleService salaryCycleService) {
        this.transactionRepository = transactionRepository;
        this.tagRepository = tagRepository;
        this.ruleDefinitionRepository = ruleDefinitionRepository;
        this.dynamicDroolsService = dynamicDroolsService;
        this.salaryCycleRepository = salaryCycleRepository;
        this.salaryCycleService = salaryCycleService;
    }

    /**
     * Clear all data from the system
     */
    @Transactional
    public Map<String, Long> clearAllData() {
        logger.info("Clearing all data from the system");

        Map<String, Long> counts = new HashMap<>();

        // Count before deleting
        long transactionCount = transactionRepository.count();
        long tagCount = tagRepository.count();
        long ruleCount = ruleDefinitionRepository.count();
        long salaryCycleCount = salaryCycleRepository.count();

        // Delete all data
        transactionRepository.deleteAll();
        tagRepository.deleteAll();
        ruleDefinitionRepository.deleteAll();
        salaryCycleRepository.deleteAll();

        // Reload rules (will be empty now)
        dynamicDroolsService.reloadRules();

        counts.put("transactions", transactionCount);
        counts.put("tags", tagCount);
        counts.put("rules", ruleCount);
        counts.put("salaryCycles", salaryCycleCount);

        logger.info("Cleared {} transactions, {} tags, {} rules, {} salary cycles",
                    transactionCount, tagCount, ruleCount, salaryCycleCount);

        return counts;
    }

    /**
     * Clear only transactions
     */
    @Transactional
    public long clearTransactions() {
        logger.info("Clearing all transactions");
        long count = transactionRepository.count();
        transactionRepository.deleteAll();

        // Also clear tags as they are derived from transactions
        tagRepository.deleteAll();

        logger.info("Cleared {} transactions", count);
        return count;
    }

    /**
     * Clear only rules
     */
    @Transactional
    public long clearRules() {
        logger.info("Clearing all rules");
        long count = ruleDefinitionRepository.count();
        ruleDefinitionRepository.deleteAll();

        // Reload rules (will be empty now)
        dynamicDroolsService.reloadRules();

        logger.info("Cleared {} rules", count);
        return count;
    }

    /**
     * Clear only tags
     */
    @Transactional
    public long clearTags() {
        logger.info("Clearing all tags");
        long count = tagRepository.count();
        tagRepository.deleteAll();
        logger.info("Cleared {} tags", count);
        return count;
    }

    /**
     * Clear only salary cycles
     */
    @Transactional
    public long clearSalaryCycles() {
        logger.info("Clearing all salary cycles");
        long count = salaryCycleRepository.count();
        salaryCycleRepository.deleteAll();
        logger.info("Cleared {} salary cycles", count);
        return count;
    }

    /**
     * Recalculate all salary cycles based on salary transactions
     */
    @Transactional
    public Map<String, Long> recalculateSalaryCycles() {
        logger.info("Recalculating all salary cycles");

        Map<String, Long> counts = new HashMap<>();
        long createdCount = 0;
        long updatedCount = 0;

        try {
            // Clear existing salary cycles
            long existingCount = salaryCycleRepository.count();
            logger.info("Clearing {} existing salary cycles before recalculation", existingCount);
            salaryCycleRepository.deleteAll();

            // Recreate salary cycles using SalaryCycleService
            // This will detect all salary transactions and create cycles
            createdCount = salaryCycleService.recalculateAllCycles();

            logger.info("Recalculated salary cycles: created {}, updated {}", createdCount, updatedCount);

            counts.put("created", createdCount);
            counts.put("updated", updatedCount);
        } catch (Exception e) {
            logger.error("Error recalculating salary cycles", e);
            throw new RuntimeException("Failed to recalculate salary cycles: " + e.getMessage(), e);
        }

        return counts;
    }
}

