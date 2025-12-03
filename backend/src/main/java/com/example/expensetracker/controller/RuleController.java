package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ImportResultDto;
import com.example.expensetracker.dto.RuleExportDto;
import com.example.expensetracker.model.RuleDefinition;
import com.example.expensetracker.service.DynamicDroolsService;
import com.example.expensetracker.service.RuleManagementService;
import com.example.expensetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    private final RuleManagementService ruleService;
    private final DynamicDroolsService droolsService;
    private final TransactionService transactionService;

    @Autowired
    public RuleController(RuleManagementService ruleService, DynamicDroolsService droolsService,
                          TransactionService transactionService) {
        this.ruleService = ruleService;
        this.droolsService = droolsService;
        this.transactionService = transactionService;
    }

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
}
