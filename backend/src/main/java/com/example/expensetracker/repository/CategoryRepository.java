package com.example.expensetracker.repository;

import com.example.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all enabled categories
     */
    List<Category> findByEnabledTrue();

    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * Check if category name already exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if category name exists excluding specific ID (for updates)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Find category by slug
     */
    Optional<Category> findBySlug(String slug);
}

