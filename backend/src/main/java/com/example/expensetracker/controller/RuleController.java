package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ImportResultDto;
import com.example.expensetracker.dto.RuleExportDto;
import com.example.expensetracker.dto.RuleMatchResult;
import com.example.expensetracker.model.RuleDefinition;
import com.example.expensetracker.service.CategorizationService;
import com.example.expensetracker.service.DynamicDroolsService;
import com.example.expensetracker.service.RuleManagementService;
import com.example.expensetracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleManagementService ruleService;
    private final DynamicDroolsService droolsService;
    private final TransactionService transactionService;
    private final CategorizationService categorizationService;

    @PostMapping
    public ResponseEntity<RuleDefinition> createRule(@RequestBody RuleDefinition rule) {
        RuleDefinition saved = ruleService.createRule(rule);
        droolsService.reloadRules();
        transactionService.recategorizeAll();
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleDefinition> updateRule(@PathVariable Long id, @RequestBody RuleDefinition rule) {
        RuleDefinition updated = ruleService.updateRule(id, rule);
        droolsService.reloadRules();
        transactionService.recategorizeAll();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        droolsService.reloadRules();
        transactionService.recategorizeAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RuleDefinition>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping("/reload")
    public ResponseEntity<String> reload() {
        droolsService.reloadRules();
        int updated = transactionService.recategorizeAll();
        return ResponseEntity.ok("Rules reloaded and " + updated + " transactions recategorized");
    }

    /**
     * Export all rules as JSON
     * GET /api/rules/export
     */
    @GetMapping("/export")
    public ResponseEntity<List<RuleExportDto>> exportRules() {
        List<RuleExportDto> exportData = ruleService.exportRules();
        return ResponseEntity.ok(exportData);
    }

    /**
     * Import rules from JSON
     * POST /api/rules/import
     * Query param: skipDuplicates (default: false) - if true, skip existing rules; if false, update them
     */
    @PostMapping("/import")
    public ResponseEntity<ImportResultDto> importRules(
            @RequestBody List<RuleExportDto> rules,
            @RequestParam(defaultValue = "false") boolean skipDuplicates) {

        ImportResultDto result = ruleService.importRules(rules, skipDuplicates);

        // Reload Drools rules and recategorize transactions after import
        droolsService.reloadRules();
        transactionService.recategorizeAll();

        return ResponseEntity.ok(result);
    }

    /**
     * Test rule matching with match scores
     * POST /api/rules/test-match
     * Body: { "description": "car loan repayment", "type": "DEBIT" }
     */
    @PostMapping("/test-match")
    public ResponseEntity<Map<String, Object>> testMatch(@RequestBody Map<String, String> request) {
        String description = request.get("description");
        String type = request.getOrDefault("type", "DEBIT");

        Map<String, Object> response = new HashMap<>();
        response.put("description", description);
        response.put("type", type);

        // Get the category using the categorization service
        String category = categorizationService.categorize(description, type);
        response.put("category", category);

        // Get the best matching rule with score
        Optional<RuleMatchResult> bestMatch = categorizationService.getBestMatchingRule(description, type);
        if (bestMatch.isPresent()) {
            RuleMatchResult match = bestMatch.get();
            Map<String, Object> matchInfo = new HashMap<>();
            matchInfo.put("ruleName", match.getRule().getRuleName());
            matchInfo.put("category", match.getRule().getCategoryName());
            matchInfo.put("pattern", match.getRule().getPattern());
            matchInfo.put("priority", match.getRule().getPriority());
            matchInfo.put("score", match.getScore());
            response.put("bestMatch", matchInfo);
        } else {
            response.put("bestMatch", null);
        }

        return ResponseEntity.ok(response);
    }
}
