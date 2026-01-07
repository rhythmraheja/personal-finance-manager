package com.finance.manager.controller;

import com.finance.manager.dto.request.CategoryRequest;
import com.finance.manager.dto.response.CategoryListResponse;
import com.finance.manager.dto.response.CategoryResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.service.CategoryService;
import com.finance.manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CategoryListResponse> getAllCategories(HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        CategoryListResponse response = categoryService.getAllCategories(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        CategoryResponse response = categoryService.createCategory(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable String name,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        MessageResponse response = categoryService.deleteCategory(name, user);
        return ResponseEntity.ok(response);
    }
}

