package com.example.expensetracker.config;

import com.example.expensetracker.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initializeData(CategoryService categoryService) {
        return args -> {
            logger.info("Initializing application data...");
            categoryService.initializeDefaultCategories();
            logger.info("Application data initialization complete");
        };
    }
}

