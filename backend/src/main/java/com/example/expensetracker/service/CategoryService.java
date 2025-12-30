package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategoryDto;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Get all categories
     */
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get only enabled categories
     */
    public List<CategoryDto> getEnabledCategories() {
        logger.info("Fetching enabled categories");
        return categoryRepository.findByEnabledTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public Optional<CategoryDto> getCategoryById(Long id) {
        logger.info("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Create new category
     */
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        logger.info("Creating new category: {}", categoryDto.getName());

        // Validate unique name
        if (categoryRepository.existsByNameIgnoreCase(categoryDto.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setSlug(generateSlug(categoryDto.getName()));
        category.setColor(categoryDto.getColor() != null ? categoryDto.getColor() : "#6b7280"); // Default gray
        category.setIcon(categoryDto.getIcon() != null ? categoryDto.getIcon() : "circle");
        category.setEnabled(categoryDto.getEnabled() != null ? categoryDto.getEnabled() : true);

        Category saved = categoryRepository.save(category);
        logger.info("Category created successfully with id: {}", saved.getId());

        return convertToDto(saved);
    }

    /**
     * Update existing category
     */
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        logger.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Validate unique name (excluding current category)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryDto.getName(), id)) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getName() + "' already exists");
        }

        // Update fields
        category.setName(categoryDto.getName());
        category.setSlug(generateSlug(categoryDto.getName()));

        if (categoryDto.getColor() != null) {
            category.setColor(categoryDto.getColor());
        }

        if (categoryDto.getIcon() != null) {
            category.setIcon(categoryDto.getIcon());
        }

        if (categoryDto.getEnabled() != null) {
            category.setEnabled(categoryDto.getEnabled());
        }

        Category updated = categoryRepository.save(category);
        logger.info("Category updated successfully: {}", updated.getName());

        return convertToDto(updated);
    }

    /**
     * Enable a category
     */
    @Transactional
    public CategoryDto enableCategory(Long id) {
        logger.info("Enabling category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        category.setEnabled(true);
        Category updated = categoryRepository.save(category);

        logger.info("Category enabled: {}", updated.getName());
        return convertToDto(updated);
    }

    /**
     * Disable a category
     */
    @Transactional
    public CategoryDto disableCategory(Long id) {
        logger.info("Disabling category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        category.setEnabled(false);
        Category updated = categoryRepository.save(category);

        logger.info("Category disabled: {}", updated.getName());
        return convertToDto(updated);
    }

    /**
     * Delete a category
     * WARNING: This permanently deletes the category from database
     */
    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Optional: Check if category is used in transactions
        // This is a safety check - you can remove this if you want to allow deletion
        // For now, we'll allow deletion and let transactions keep their category names as strings

        categoryRepository.delete(category);
        logger.info("Category deleted: {}", category.getName());
    }

    /**
     * Generate slug from category name
     * Example: "Food & Dining" -> "food-dining"
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-")          // Replace spaces with hyphens
                .replaceAll("-+", "-")            // Replace multiple hyphens with single
                .trim();
    }

    /**
     * Convert Category entity to DTO
     */
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        dto.setEnabled(category.getEnabled());
        return dto;
    }

    /**
     * Initialize default categories if none exist
     */
    @Transactional
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            logger.info("Initializing default categories...");

            String[][] defaultCategories = {
                {"Income", "#10b981", "trending-up"},
                {"Food & Dining", "#f97316", "utensils"},
                {"Groceries", "#22c55e", "shopping-basket"},
                {"Shopping", "#a855f7", "shopping-bag"},
                {"Travel", "#3b82f6", "plane"},
                {"Bills & Utilities", "#eab308", "file-text"},
                {"Medical & Health", "#ec4899", "heart-pulse"},
                {"Personal Care", "#f59e0b", "sparkles"},
                {"Subscriptions", "#8b5cf6", "repeat"},
                {"Loans & EMIs", "#ef4444", "landmark"},
                {"Transfers", "#06b6d4", "arrow-right-left"},
                {"Fees & Charges", "#fb923c", "receipt"},
                {"Donations", "#14b8a6", "hand-heart"},
                {"Business", "#6366f1", "briefcase"},
                {"Fuel", "#dc2626", "fuel"},
                {"Housing / Rent", "#4f46e5", "home"},
                {"Entertainment", "#7c3aed", "tv"},
                {"Insurance", "#0891b2", "shield"},
                {"Investment", "#059669", "trending-up"},
                {"Education", "#2563eb", "graduation-cap"},
                {"Pets", "#f43f5e", "dog"},
                {"Vehicle/Transportation", "#0284c7", "car"},
                {"Credit Card Payment", "#64748b", "credit-card"},
                {"Miscellaneous", "#6b7280", "more-horizontal"},
                    {"Salary", "#059669", "more-horizontal"}
            };

            for (String[] cat : defaultCategories) {
                Category category = new Category();
                category.setName(cat[0]);
                category.setSlug(generateSlug(cat[0]));
                category.setColor(cat[1]);
                category.setIcon(cat[2]);
                category.setEnabled(true);
                categoryRepository.save(category);
            }

            logger.info("Default categories initialized successfully");
        }
    }
}

