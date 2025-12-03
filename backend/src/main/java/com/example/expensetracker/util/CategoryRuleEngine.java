package com.example.expensetracker.util;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CategoryRuleEngine {

    private final Map<String, List<String>> categoryKeywords;

    public CategoryRuleEngine() {
        this.categoryKeywords = new HashMap<>();
        initializeRules();
    }

    private void initializeRules() {
        // Food & Dining
        categoryKeywords.put("Food", Arrays.asList(
                "swiggy", "zomato", "dominos", "kfc", "pizza hut", "mcdonald",
                "burger king", "subway", "starbucks", "cafe", "restaurant",
                "food", "dining", "eatery", "pizza", "biryani"
        ));

        // Groceries
        categoryKeywords.put("Groceries", Arrays.asList(
                "dmart", "bigbasket", "reliance fresh", "more", "supermarket",
                "grocery", "vegetables", "fruits", "market", "blinkit",
                "instamart", "zepto", "dunzo"
        ));

        // Shopping
        categoryKeywords.put("Shopping", Arrays.asList(
                "amazon", "flipkart", "myntra", "ajio", "meesho", "snapdeal",
                "nykaa", "shopping", "mall", "retail", "store", "fashion",
                "clothing", "apparel", "accessories"
        ));

        // Travel
        categoryKeywords.put("Travel", Arrays.asList(
                "uber", "ola", "rapido", "irctc", "makemytrip", "goibibo",
                "redbus", "yatra", "cleartrip", "travel", "flight", "hotel",
                "train", "bus", "cab", "taxi", "airport"
        ));

        // Income
        categoryKeywords.put("Income", Arrays.asList(
                "salary", "credited", "neft cr", "imps cr", "ach cr", "rtgs cr",
                "credit", "refund", "cashback", "interest", "dividend",
                "bonus", "income", "payment received"
        ));

        // Bills & Utilities
        categoryKeywords.put("Bills", Arrays.asList(
                "electricity", "water bill", "gas bill", "postpaid", "prepaid",
                "recharge", "mobile", "broadband", "internet", "wifi", "utility",
                "bill payment", "bses", "adani", "airtel", "jio", "vodafone"
        ));

        // Fuel
        categoryKeywords.put("Fuel", Arrays.asList(
                "petrol", "diesel", "fuel", "hpcl", "bpcl", "iocl", "shell",
                "essar", "gas station", "cng", "petroleum"
        ));

        // Medical & Health
        categoryKeywords.put("Medical", Arrays.asList(
                "hospital", "pharmacy", "apollo", "medplus", "clinic", "doctor",
                "medical", "medicine", "health", "diagnostic", "lab", "pharma",
                "wellness", "healthcare"
        ));

        // Rent
        categoryKeywords.put("Rent", Arrays.asList(
                "rent", "lease", "housing", "apartment", "flat rent", "house rent"
        ));

        // Entertainment
        categoryKeywords.put("Entertainment", Arrays.asList(
                "netflix", "hotstar", "prime video", "sony liv", "zee5", "disney",
                "youtube", "spotify", "amazon prime", "movie", "cinema", "pvr",
                "inox", "entertainment", "gaming", "game", "subscription"
        ));

        // Insurance
        categoryKeywords.put("Insurance", Arrays.asList(
                "insurance", "lic", "policy", "premium", "health insurance",
                "life insurance", "car insurance"
        ));

        // Investment
        categoryKeywords.put("Investment", Arrays.asList(
                "mutual fund", "sip", "stock", "equity", "investment", "zerodha",
                "groww", "upstox", "trading", "shares"
        ));

        // Education
        categoryKeywords.put("Education", Arrays.asList(
                "education", "school", "college", "university", "course", "tuition",
                "udemy", "coursera", "books", "study", "training"
        ));
    }

    /**
     * Find matching category for given keywords
     */
    public String findCategory(String cleanedDescription) {
        if (cleanedDescription == null || cleanedDescription.isEmpty()) {
            return "Miscellaneous";
        }

        String lowerDesc = cleanedDescription.toLowerCase();

        // Check each category's keywords
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (lowerDesc.contains(keyword)) {
                    return category;
                }
            }
        }

        return "Miscellaneous";
    }

    /**
     * Get all defined categories
     */
    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>(categoryKeywords.keySet());
        categories.add("Miscellaneous");
        return categories;
    }
}


