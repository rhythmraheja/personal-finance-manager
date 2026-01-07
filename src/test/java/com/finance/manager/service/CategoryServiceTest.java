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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category defaultCategory;
    private Category customCategory;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        defaultCategory = Category.builder()
                .id(1L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .user(null)
                .build();

        customCategory = Category.builder()
                .id(2L)
                .name("Freelance")
                .type(TransactionType.INCOME)
                .custom(true)
                .user(user)
                .build();
    }

    @Test
    void getAllCategories_Success() {
        when(categoryRepository.findByUserIsNull()).thenReturn(Arrays.asList(defaultCategory));
        when(categoryRepository.findByUser(user)).thenReturn(Arrays.asList(customCategory));

        CategoryListResponse response = categoryService.getAllCategories(user);

        assertNotNull(response);
        assertEquals(2, response.getCategories().size());
    }

    @Test
    void createCategory_Success() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Investment")
                .type("INCOME")
                .build();

        when(categoryRepository.existsByNameForUser(anyString(), any(User.class))).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);

        CategoryResponse response = categoryService.createCategory(request, user);

        assertNotNull(response);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Freelance")
                .type("INCOME")
                .build();

        when(categoryRepository.existsByNameForUser(anyString(), any(User.class))).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request, user));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findByNameAndUserIsNull("Freelance")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUser("Freelance", user)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.existsByUserAndCategory(user, "Freelance")).thenReturn(false);

        MessageResponse response = categoryService.deleteCategory("Freelance", user);

        assertNotNull(response);
        assertEquals("Category deleted successfully", response.getMessage());
        verify(categoryRepository).delete(customCategory);
    }

    @Test
    void deleteCategory_DefaultCategory_ThrowsException() {
        when(categoryRepository.findByNameAndUserIsNull("Salary")).thenReturn(Optional.of(defaultCategory));

        assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory("Salary", user));
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByNameAndUserIsNull("Unknown")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUser("Unknown", user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory("Unknown", user));
    }

    @Test
    void deleteCategory_InUse_ThrowsException() {
        when(categoryRepository.findByNameAndUserIsNull("Freelance")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUser("Freelance", user)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.existsByUserAndCategory(user, "Freelance")).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> categoryService.deleteCategory("Freelance", user));
    }

    @Test
    void findCategoryByName_Success() {
        when(categoryRepository.findByNameAndUser("Salary", user)).thenReturn(Optional.of(defaultCategory));

        Category result = categoryService.findCategoryByName("Salary", user);

        assertNotNull(result);
        assertEquals("Salary", result.getName());
    }

    @Test
    void findCategoryByName_NotFound_ThrowsException() {
        when(categoryRepository.findByNameAndUser("Unknown", user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryByName("Unknown", user));
    }

    @Test
    void categoryExistsForUser_ReturnsTrue() {
        when(categoryRepository.existsByNameForUser("Salary", user)).thenReturn(true);

        assertTrue(categoryService.categoryExistsForUser("Salary", user));
    }

    @Test
    void categoryExistsForUser_ReturnsFalse() {
        when(categoryRepository.existsByNameForUser("Unknown", user)).thenReturn(false);

        assertFalse(categoryService.categoryExistsForUser("Unknown", user));
    }
}

