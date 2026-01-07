package com.finance.manager.config;

import com.finance.manager.entity.Category;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        List<Category> existingDefaults = categoryRepository.findByUserIsNull();
        if (!existingDefaults.isEmpty()) {
            log.info("Default categories already exist, skipping initialization");
            return;
        }

        log.info("Initializing default categories...");

        Category salary = Category.builder()
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .user(null)
                .build();

        Category food = Category.builder()
                .name("Food")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        Category rent = Category.builder()
                .name("Rent")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        Category transportation = Category.builder()
                .name("Transportation")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        Category entertainment = Category.builder()
                .name("Entertainment")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        Category healthcare = Category.builder()
                .name("Healthcare")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        Category utilities = Category.builder()
                .name("Utilities")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .user(null)
                .build();

        categoryRepository.saveAll(List.of(
            salary, food, rent, transportation, entertainment, healthcare, utilities
        ));

        log.info("Default categories initialized successfully");
    }
}

