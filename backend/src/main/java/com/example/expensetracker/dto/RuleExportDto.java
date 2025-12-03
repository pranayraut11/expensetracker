package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleExportDto {
    private String ruleName;
    private String categoryName;
    private String pattern;
    private Integer priority;
    private Boolean enabled;
    private Boolean includeInTotals;
}

