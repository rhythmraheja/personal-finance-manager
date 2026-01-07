package com.finance.manager.dto;

import com.finance.manager.dto.request.*;
import com.finance.manager.dto.response.*;
import com.finance.manager.enums.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void registerRequest_Builder() {
        RegisterRequest req = RegisterRequest.builder()
                .username("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();

        assertEquals("test@example.com", req.getUsername());
        assertEquals("password123", req.getPassword());
        assertEquals("Test User", req.getFullName());
        assertEquals("+1234567890", req.getPhoneNumber());
    }

    @Test
    void loginRequest_Builder() {
        LoginRequest req = LoginRequest.builder()
                .username("test@example.com")
                .password("password123")
                .build();

        assertEquals("test@example.com", req.getUsername());
        assertEquals("password123", req.getPassword());
    }

    @Test
    void transactionRequest_Builder() {
        TransactionRequest req = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Monthly salary")
                .build();

        assertEquals(new BigDecimal("5000.00"), req.getAmount());
        assertEquals("2024-01-15", req.getDate());
        assertEquals("Salary", req.getCategory());
        assertEquals("Monthly salary", req.getDescription());
    }

    @Test
    void transactionUpdateRequest_Builder() {
        TransactionUpdateRequest req = TransactionUpdateRequest.builder()
                .amount(new BigDecimal("6000.00"))
                .category("Bonus")
                .description("Updated")
                .build();

        assertEquals(new BigDecimal("6000.00"), req.getAmount());
        assertEquals("Bonus", req.getCategory());
        assertEquals("Updated", req.getDescription());
    }

    @Test
    void categoryRequest_Builder() {
        CategoryRequest req = CategoryRequest.builder()
                .name("Freelance")
                .type("INCOME")
                .build();

        assertEquals("Freelance", req.getName());
        assertEquals("INCOME", req.getType());
    }

    @Test
    void goalRequest_Builder() {
        GoalRequest req = GoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2025-01-01")
                .startDate("2024-01-01")
                .build();

        assertEquals("Emergency Fund", req.getGoalName());
        assertEquals(new BigDecimal("10000.00"), req.getTargetAmount());
        assertEquals("2025-01-01", req.getTargetDate());
        assertEquals("2024-01-01", req.getStartDate());
    }

    @Test
    void goalUpdateRequest_Builder() {
        GoalUpdateRequest req = GoalUpdateRequest.builder()
                .targetAmount(new BigDecimal("15000.00"))
                .targetDate("2026-01-01")
                .build();

        assertEquals(new BigDecimal("15000.00"), req.getTargetAmount());
        assertEquals("2026-01-01", req.getTargetDate());
    }

    @Test
    void messageResponse() {
        MessageResponse res = MessageResponse.of("Success");
        assertEquals("Success", res.getMessage());
    }

    @Test
    void registerResponse() {
        RegisterResponse res = RegisterResponse.success(1L);
        assertEquals("User registered successfully", res.getMessage());
        assertEquals(1L, res.getUserId());
    }

    @Test
    void transactionResponse_Builder() {
        TransactionResponse res = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Monthly")
                .type(TransactionType.INCOME)
                .build();

        assertEquals(1L, res.getId());
        assertEquals(new BigDecimal("5000.00"), res.getAmount());
        assertEquals(TransactionType.INCOME, res.getType());
    }

    @Test
    void transactionListResponse_Builder() {
        TransactionResponse tx = TransactionResponse.builder().id(1L).build();
        TransactionListResponse res = TransactionListResponse.builder()
                .transactions(List.of(tx))
                .build();

        assertEquals(1, res.getTransactions().size());
    }

    @Test
    void categoryResponse_Builder() {
        CategoryResponse res = CategoryResponse.builder()
                .name("Salary")
                .type(TransactionType.INCOME)
                .custom(false)
                .build();

        assertEquals("Salary", res.getName());
        assertEquals(TransactionType.INCOME, res.getType());
        assertFalse(res.isCustom());
    }

    @Test
    void categoryListResponse_Builder() {
        CategoryResponse cat = CategoryResponse.builder().name("Salary").build();
        CategoryListResponse res = CategoryListResponse.builder()
                .categories(List.of(cat))
                .build();

        assertEquals(1, res.getCategories().size());
    }

    @Test
    void goalResponse_Builder() {
        GoalResponse res = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2025-01-01")
                .startDate("2024-01-01")
                .currentProgress(new BigDecimal("2000.00"))
                .progressPercentage(new BigDecimal("20.00"))
                .remainingAmount(new BigDecimal("8000.00"))
                .build();

        assertEquals(1L, res.getId());
        assertEquals("Emergency Fund", res.getGoalName());
    }

    @Test
    void goalListResponse_Builder() {
        GoalResponse goal = GoalResponse.builder().id(1L).build();
        GoalListResponse res = GoalListResponse.builder()
                .goals(List.of(goal))
                .build();

        assertEquals(1, res.getGoals().size());
    }

    @Test
    void monthlyReportResponse_Builder() {
        MonthlyReportResponse res = MonthlyReportResponse.builder()
                .month(1)
                .year(2024)
                .totalIncome(Map.of("Salary", new BigDecimal("5000.00")))
                .totalExpenses(Map.of("Food", new BigDecimal("500.00")))
                .netSavings(new BigDecimal("4500.00"))
                .build();

        assertEquals(1, res.getMonth());
        assertEquals(2024, res.getYear());
        assertEquals(new BigDecimal("4500.00"), res.getNetSavings());
    }

    @Test
    void yearlyReportResponse_Builder() {
        YearlyReportResponse res = YearlyReportResponse.builder()
                .year(2024)
                .totalIncome(Map.of("Salary", new BigDecimal("60000.00")))
                .totalExpenses(Map.of("Rent", new BigDecimal("14400.00")))
                .netSavings(new BigDecimal("45600.00"))
                .build();

        assertEquals(2024, res.getYear());
        assertEquals(new BigDecimal("45600.00"), res.getNetSavings());
    }

    @Test
    void errorResponse_Builder() {
        String timestamp = Instant.now().toString();
        ErrorResponse res = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .timestamp(timestamp)
                .build();

        assertEquals(400, res.getStatus());
        assertEquals("Bad Request", res.getError());
        assertEquals("Invalid input", res.getMessage());
        assertEquals(timestamp, res.getTimestamp());
    }

    @Test
    void errorResponse_StaticOf() {
        ErrorResponse res = ErrorResponse.of(404, "Not Found", "Resource not found", "/api/test");
        assertEquals(404, res.getStatus());
        assertEquals("Not Found", res.getError());
        assertNotNull(res.getTimestamp());
    }
}
