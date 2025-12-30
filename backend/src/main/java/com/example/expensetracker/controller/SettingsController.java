package com.example.expensetracker.controller;

import com.example.expensetracker.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Clear all data (transactions, tags, rules)
     */
    @DeleteMapping("/clear-all-data")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        Map<String, Long> deletedCounts = settingsService.clearAllData();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All data cleared successfully");
        response.put("deletedCounts", deletedCounts);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear only transactions
     */
    @DeleteMapping("/clear-transactions")
    public ResponseEntity<Map<String, Object>> clearTransactions() {
        long count = settingsService.clearTransactions();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All transactions cleared successfully");
        response.put("deletedCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear only rules
     */
    @DeleteMapping("/clear-rules")
    public ResponseEntity<Map<String, Object>> clearRules() {
        long count = settingsService.clearRules();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All rules cleared successfully");
        response.put("deletedCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear only tags
     */
    @DeleteMapping("/clear-tags")
    public ResponseEntity<Map<String, Object>> clearTags() {
        long count = settingsService.clearTags();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All tags cleared successfully");
        response.put("deletedCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear only salary cycles
     */
    @DeleteMapping("/clear-salary-cycles")
    public ResponseEntity<Map<String, Object>> clearSalaryCycles() {
        long count = settingsService.clearSalaryCycles();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All salary cycles cleared successfully");
        response.put("deletedCount", count);

        return ResponseEntity.ok(response);
    }

    /**
     * Recalculate salary cycles based on salary transactions
     */
    @PostMapping("/recalculate-salary-cycles")
    public ResponseEntity<Map<String, Object>> recalculateSalaryCycles() {
        Map<String, Long> counts = settingsService.recalculateSalaryCycles();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Salary cycles recalculated successfully");
        response.put("createdCount", counts.get("created"));
        response.put("updatedCount", counts.get("updated"));

        return ResponseEntity.ok(response);
    }
}
