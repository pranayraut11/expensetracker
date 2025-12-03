package com.example.expensetracker.service;

import com.example.expensetracker.util.CategoryRuleEngine;
import com.example.expensetracker.util.DescriptionCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategorizationService {

    private final CategoryRuleEngine categoryRuleEngine;

    @Autowired
    public CategorizationService(CategoryRuleEngine categoryRuleEngine) {
        this.categoryRuleEngine = categoryRuleEngine;
    }

    /**
     * Categorize a transaction based on description and type
     */
    public String categorize(String rawDescription, String type) {
        if (rawDescription == null || rawDescription.isEmpty()) {
            return "Miscellaneous";
        }

        // Clean the description first
        String cleanedDescription = DescriptionCleaner.clean(rawDescription);

        // For CREDIT transactions, check if it's income-related
        if ("CREDIT".equalsIgnoreCase(type)) {
            String lowerDesc = cleanedDescription.toLowerCase();
            if (lowerDesc.contains("salary") ||
                lowerDesc.contains("credited") ||
                lowerDesc.contains("neft cr") ||
                lowerDesc.contains("imps cr") ||
                lowerDesc.contains("ach cr") ||
                lowerDesc.contains("rtgs cr") ||
                rawDescription.toLowerCase().contains("salary")) {
                return "Income";
            }
        }

        // Use the rule engine to find matching category
        String category = categoryRuleEngine.findCategory(cleanedDescription);

        // Also check against the original description for better matching
        if (category.equals("Miscellaneous")) {
            category = categoryRuleEngine.findCategory(rawDescription);
        }

        return category;
    }
}


