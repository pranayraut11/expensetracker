package com.example.expensetracker.service;

import com.example.expensetracker.dto.ImportResultDto;
import com.example.expensetracker.dto.RuleExportDto;
import com.example.expensetracker.model.RuleDefinition;
import com.example.expensetracker.repository.RuleDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RuleManagementService {

    private final RuleDefinitionRepository repository;

    @Autowired
    public RuleManagementService(RuleDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RuleDefinition createRule(RuleDefinition rule) {
        return repository.save(rule);
    }

    @Transactional
    public RuleDefinition updateRule(Long id, RuleDefinition updated) {
        RuleDefinition existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));
        existing.setRuleName(updated.getRuleName());
        existing.setCategoryName(updated.getCategoryName());
        existing.setPattern(updated.getPattern());
        existing.setPriority(updated.getPriority());
        existing.setEnabled(updated.getEnabled());
        existing.setIncludeInTotals(updated.getIncludeInTotals());
        return repository.save(existing);
    }

    @Transactional
    public void deleteRule(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RuleDefinition> getAllRules() {
        return repository.findAll();
    }

    public RuleDefinition saveRule(RuleDefinition rule) {
        return repository.save(rule);
    }

    /**
     * Build DRL content from rules.
     */
    public String buildDRLStringFromRules(List<RuleDefinition> rules) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.expensetracker.rules\n");
        sb.append("import com.example.expensetracker.model.Transaction;\n\n");
        for (RuleDefinition r : rules) {
            if (Boolean.TRUE.equals(r.getEnabled())) {
                sb.append("rule \"").append(escape(r.getRuleName())).append("\"\n");
                sb.append("    salience ").append(r.getPriority() == null ? 0 : r.getPriority()).append("\n");
                sb.append("when\n");
                sb.append("    t : Transaction( description matches (\"(?i).*")
                  .append(escapeForRegex(r.getPattern())).append(".*\") )\n");
                sb.append("then\n");
                sb.append("    t.setCategory(\"").append(escape(r.getCategoryName())).append("\");\n");
                // Set includeInTotals based on rule configuration
                boolean includeInTotals = r.getIncludeInTotals() != null ? r.getIncludeInTotals() : true;
                sb.append("    t.setIncludeInTotals(").append(includeInTotals).append(");\n");
                sb.append("end\n\n");
            }
        }
        return sb.toString();
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    private String escapeForRegex(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Export all rules as DTOs (without IDs and timestamps)
     */
    @Transactional(readOnly = true)
    public List<RuleExportDto> exportRules() {
        List<RuleDefinition> rules = repository.findAll();
        return rules.stream()
                .map(this::convertToExportDto)
                .collect(Collectors.toList());
    }

    /**
     * Import rules from DTOs
     */
    @Transactional
    public ImportResultDto importRules(List<RuleExportDto> ruleDtos, boolean skipDuplicates) {
        int successCount = 0;
        int errorCount = 0;
        int skippedCount = 0;
        List<String> errors = new ArrayList<>();

        for (RuleExportDto dto : ruleDtos) {
            try {
                // Check for duplicate rule name
                Optional<RuleDefinition> existing = repository.findByRuleName(dto.getRuleName());

                if (existing.isPresent()) {
                    if (skipDuplicates) {
                        skippedCount++;
                        continue;
                    } else {
                        // Update existing rule
                        RuleDefinition rule = existing.get();
                        rule.setCategoryName(dto.getCategoryName());
                        rule.setPattern(dto.getPattern());
                        rule.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
                        rule.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
                        repository.save(rule);
                        successCount++;
                    }
                } else {
                    // Create new rule
                    RuleDefinition newRule = convertFromExportDto(dto);
                    repository.save(newRule);
                    successCount++;
                }
            } catch (Exception e) {
                errorCount++;
                errors.add(dto.getRuleName() + ": " + e.getMessage());
            }
        }

        String message = String.format("Import completed: %d imported, %d skipped, %d errors",
                                       successCount, skippedCount, errorCount);
        if (!errors.isEmpty() && errors.size() <= 5) {
            message += ". Errors: " + String.join("; ", errors);
        }

        return new ImportResultDto(successCount, errorCount, skippedCount, message);
    }

    private RuleExportDto convertToExportDto(RuleDefinition rule) {
        RuleExportDto dto = new RuleExportDto();
        dto.setRuleName(rule.getRuleName());
        dto.setCategoryName(rule.getCategoryName());
        dto.setPattern(rule.getPattern());
        dto.setPriority(rule.getPriority());
        dto.setEnabled(rule.getEnabled());
        dto.setIncludeInTotals(rule.getIncludeInTotals());
        return dto;
    }

    private RuleDefinition convertFromExportDto(RuleExportDto dto) {
        RuleDefinition rule = new RuleDefinition();
        rule.setRuleName(dto.getRuleName());
        rule.setCategoryName(dto.getCategoryName());
        rule.setPattern(dto.getPattern());
        rule.setPriority(dto.getPriority() != null ? dto.getPriority() : 0);
        rule.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        return rule;
    }
}

