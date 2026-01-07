package com.finance.manager.controller;

import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.GoalUpdateRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.service.GoalService;
import com.finance.manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody GoalRequest request,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        GoalResponse response = goalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<GoalListResponse> getAllGoals(HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        GoalListResponse response = goalService.getAllGoals(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
            @PathVariable Long id,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        GoalResponse response = goalService.getGoal(id, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalUpdateRequest request,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        GoalResponse response = goalService.updateGoal(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(
            @PathVariable Long id,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        MessageResponse response = goalService.deleteGoal(id, user);
        return ResponseEntity.ok(response);
    }
}

