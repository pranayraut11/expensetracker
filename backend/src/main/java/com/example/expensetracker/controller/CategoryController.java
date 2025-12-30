package com.example.expensetracker.controller;

import com.example.expensetracker.dto.CategoryDto;
import com.example.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories (enabled and disabled)
     *
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        logger.info("GET /api/categories - Fetching all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get only enabled categories
     *
     * GET /api/categories/enabled
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<CategoryDto>> getEnabledCategories() {
        logger.info("GET /api/categories/enabled - Fetching enabled categories");
        List<CategoryDto> categories = categoryService.getEnabledCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID
     *
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        logger.info("GET /api/categories/{} - Fetching category", id);
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new category
     *
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        logger.info("POST /api/categories - Creating new category: {}", categoryDto.getName());

        try {
            CategoryDto created = categoryService.createCategory(categoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update existing category
     *
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto categoryDto) {
        logger.info("PUT /api/categories/{} - Updating category", id);

        try {
            CategoryDto updated = categoryService.updateCategory(id, categoryDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Enable a category
     *
     * PATCH /api/categories/{id}/enable
     */
    @PatchMapping("/{id}/enable")
    public ResponseEntity<CategoryDto> enableCategory(@PathVariable Long id) {
        logger.info("PATCH /api/categories/{}/enable - Enabling category", id);

        try {
            CategoryDto enabled = categoryService.enableCategory(id);
            return ResponseEntity.ok(enabled);
        } catch (IllegalArgumentException e) {
            logger.error("Error enabling category: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Disable a category
     *
     * PATCH /api/categories/{id}/disable
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<CategoryDto> disableCategory(@PathVariable Long id) {
        logger.info("PATCH /api/categories/{}/disable - Disabling category", id);

        try {
            CategoryDto disabled = categoryService.disableCategory(id);
            return ResponseEntity.ok(disabled);
        } catch (IllegalArgumentException e) {
            logger.error("Error disabling category: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a category
     * 
     * DELETE /api/categories/{id}
     * 
     * WARNING: This will permanently delete the category.
     * Use disable instead if you want to keep historical data.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        logger.info("DELETE /api/categories/{} - Deleting category", id);
        
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Initialize default categories
     *
     * POST /api/categories/initialize
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeCategories() {
        logger.info("POST /api/categories/initialize - Initializing default categories");
        categoryService.initializeDefaultCategories();
        return ResponseEntity.ok("Default categories initialized successfully");
    }
}

