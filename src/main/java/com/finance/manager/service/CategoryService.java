package com.finance.manager.service;

import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.DuplicateResourceException;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.InvalidRequestException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public CategoryListResponse getAllCategories(User user) {
        List<Category> defaultCategories = categoryRepository.findByUserIsNull();
        List<Category> userCategories = categoryRepository.findByUser(user);

        List<CategoryResponse> responses = defaultCategories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());

        responses.addAll(userCategories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList()));

        return CategoryListResponse.of(responses);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        TransactionType type = TransactionType.valueOf(request.getType());

        if (categoryRepository.existsByNameForUser(request.getName(), user)) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .type(type)
                .custom(true)
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created: {} for user: {}", saved.getName(), user.getUsername());

        return CategoryResponse.fromEntity(saved);
    }

    @Transactional
    public MessageResponse deleteCategory(String name, User user) {
        Optional<Category> defaultCategory = categoryRepository.findByNameAndUserIsNull(name);
        if (defaultCategory.isPresent()) {
            throw new ForbiddenException("Cannot delete default category");
        }

        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", name));

        if (!category.isCustom()) {
            throw new ForbiddenException("Cannot delete default category");
        }

        if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Cannot delete category belonging to another user");
        }

        if (transactionRepository.existsByUserAndCategory(user, name)) {
            throw new InvalidRequestException("Cannot delete category that is in use by transactions");
        }

        categoryRepository.delete(category);
        log.info("Category deleted: {} for user: {}", name, user.getUsername());

        return MessageResponse.of("Category deleted successfully");
    }

    @Transactional(readOnly = true)
    public Category findCategoryByName(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", name));
    }

    @Transactional(readOnly = true)
    public boolean categoryExistsForUser(String name, User user) {
        return categoryRepository.existsByNameForUser(name, user);
    }
}

