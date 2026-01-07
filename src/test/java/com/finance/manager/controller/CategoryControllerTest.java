package com.finance.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.*;
import com.finance.manager.service.CategoryService;
import com.finance.manager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryController categoryController;

    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).username("test@example.com").build();
    }

    @Test
    void getAllCategories_Success() throws Exception {
        CategoryResponse cat1 = CategoryResponse.builder()
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .build();

        CategoryResponse cat2 = CategoryResponse.builder()
                .name("Food")
                .type(TransactionType.EXPENSE)
                .custom(false)
                .build();

        CategoryListResponse response = CategoryListResponse.builder()
                .categories(List.of(cat1, cat2))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.getAllCategories(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].name").value("Salary"));
    }

    @Test
    void getAllCategories_Unauthorized() throws Exception {
        when(userService.getAuthenticatedUser(any()))
                .thenThrow(new UnauthorizedException("Not authenticated"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCategory_Success() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Freelance")
                .type("INCOME")
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .name("Freelance")
                .type(TransactionType.INCOME)
                .custom(true)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.createCategory(any(CategoryRequest.class), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Freelance"))
                .andExpect(jsonPath("$.custom").value(true));
    }

    @Test
    void createCategory_DuplicateName() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Salary")
                .type("INCOME")
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.createCategory(any(CategoryRequest.class), any(User.class)))
                .thenThrow(new DuplicateResourceException("Category", "name", "Salary"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createCategory_InvalidRequest() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("")
                .type("")
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_Success() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.deleteCategory(eq("Freelance"), any(User.class)))
                .thenReturn(MessageResponse.of("Category deleted successfully"));

        mockMvc.perform(delete("/api/categories/Freelance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void deleteCategory_NotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.deleteCategory(eq("NonExistent"), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Category", "name", "NonExistent"));

        mockMvc.perform(delete("/api/categories/NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_Forbidden() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.deleteCategory(eq("Salary"), any(User.class)))
                .thenThrow(new ForbiddenException("Cannot delete default category"));

        mockMvc.perform(delete("/api/categories/Salary"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_InUse() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(categoryService.deleteCategory(eq("Food"), any(User.class)))
                .thenThrow(new InvalidRequestException("Category is in use by transactions"));

        mockMvc.perform(delete("/api/categories/Food"))
                .andExpect(status().isBadRequest());
    }
}
