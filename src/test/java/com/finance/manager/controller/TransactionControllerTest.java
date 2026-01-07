package com.finance.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.TransactionUpdateRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.GlobalExceptionHandler;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.exception.UnauthorizedException;
import com.finance.manager.service.TransactionService;
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
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).username("test@example.com").build();
    }

    @Test
    void createTransaction_Success() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Monthly salary")
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Monthly salary")
                .type(TransactionType.INCOME)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.createTransaction(any(TransactionRequest.class), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.category").value("Salary"));
    }

    @Test
    void createTransaction_Unauthorized() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .build();

        when(userService.getAuthenticatedUser(any()))
                .thenThrow(new UnauthorizedException("Not authenticated"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTransactions_Success() throws Exception {
        TransactionResponse tx = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .type(TransactionType.INCOME)
                .build();

        TransactionListResponse response = TransactionListResponse.builder()
                .transactions(List.of(tx))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.getAllTransactions(any(User.class), any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions[0].id").value(1));
    }

    @Test
    void getAllTransactions_WithFilters() throws Exception {
        TransactionListResponse response = TransactionListResponse.builder()
                .transactions(List.of())
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.getAllTransactions(any(User.class), eq("2024-01-01"), eq("2024-12-31"), eq("Salary")))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .param("category", "Salary"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransaction_Success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .type(TransactionType.INCOME)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.getTransaction(eq(1L), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTransaction_NotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.getTransaction(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Transaction", "id", 999L));

        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransaction_Success() throws Exception {
        TransactionUpdateRequest request = TransactionUpdateRequest.builder()
                .amount(new BigDecimal("6000.00"))
                .description("Updated salary")
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("6000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Updated salary")
                .type(TransactionType.INCOME)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.updateTransaction(eq(1L), any(TransactionUpdateRequest.class), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(6000.00));
    }

    @Test
    void deleteTransaction_Success() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.deleteTransaction(eq(1L), any(User.class)))
                .thenReturn(MessageResponse.of("Transaction deleted successfully"));

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    void deleteTransaction_NotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(transactionService.deleteTransaction(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Transaction", "id", 999L));

        mockMvc.perform(delete("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }
}
