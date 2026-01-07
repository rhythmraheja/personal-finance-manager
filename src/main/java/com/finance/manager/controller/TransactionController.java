package com.finance.manager.controller;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.TransactionUpdateRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.User;
import com.finance.manager.service.TransactionService;
import com.finance.manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        TransactionResponse response = transactionService.createTransaction(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TransactionListResponse> getAllTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        TransactionListResponse response = transactionService.getAllTransactions(user, startDate, endDate, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        TransactionResponse response = transactionService.getTransaction(id, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        TransactionResponse response = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTransaction(
            @PathVariable Long id,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        MessageResponse response = transactionService.deleteTransaction(id, user);
        return ResponseEntity.ok(response);
    }
}

