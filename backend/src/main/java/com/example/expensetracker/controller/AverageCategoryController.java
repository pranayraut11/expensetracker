package com.example.expensetracker.controller;

import com.example.expensetracker.dto.AverageCategoryDto;
import com.example.expensetracker.service.AverageCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics/average-category")
public class AverageCategoryController {

    private final AverageCategoryService averageCategoryService;

    @Autowired
    public AverageCategoryController(AverageCategoryService averageCategoryService) {
        this.averageCategoryService = averageCategoryService;
    }

    /**
     * Get average monthly income and expense by category
     * @param year Year for analysis (optional, defaults to current year)
     * @param months Number of months to analyze (optional, defaults to 12)
     * @return List of categories with average monthly income and expense
     */
    @GetMapping
    public ResponseEntity<List<AverageCategoryDto>> getAverageCategoryData(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false, defaultValue = "12") Integer months) {

        List<AverageCategoryDto> data = averageCategoryService.calculateAverageByCategory(year, months);
        return ResponseEntity.ok(data);
    }
}

