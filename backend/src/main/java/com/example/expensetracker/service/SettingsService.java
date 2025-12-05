package com.example.expensetracker.service;

import com.example.expensetracker.repository.RuleDefinitionRepository;
import com.example.expensetracker.repository.TagRepository;
import com.example.expensetracker.repository.TransactionRepository;
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

    @Autowired
    public SettingsService(TransactionRepository transactionRepository,
                          TagRepository tagRepository,
                          RuleDefinitionRepository ruleDefinitionRepository,
                          DynamicDroolsService dynamicDroolsService) {
        this.transactionRepository = transactionRepository;
        this.tagRepository = tagRepository;
        this.ruleDefinitionRepository = ruleDefinitionRepository;
        this.dynamicDroolsService = dynamicDroolsService;
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

        // Delete all data
        transactionRepository.deleteAll();
        tagRepository.deleteAll();
        ruleDefinitionRepository.deleteAll();

        // Reload rules (will be empty now)
        dynamicDroolsService.reloadRules();

        counts.put("transactions", transactionCount);
        counts.put("tags", tagCount);
        counts.put("rules", ruleCount);

        logger.info("Cleared {} transactions, {} tags, {} rules",
                    transactionCount, tagCount, ruleCount);

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
}

