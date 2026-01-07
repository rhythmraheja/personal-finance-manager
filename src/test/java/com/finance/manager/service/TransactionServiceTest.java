package com.finance.manager.service;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.TransactionUpdateRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.InvalidRequestException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;
    private Transaction transaction;
    private TransactionRequest transactionRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Salary")
                .type(TransactionType.INCOME)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Salary")
                .type(TransactionType.INCOME)
                .description("Monthly salary")
                .user(user)
                .build();

        transactionRequest = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date("2024-01-15")
                .category("Salary")
                .description("Monthly salary")
                .build();
    }

    @Test
    void createTransaction_Success() {
        when(categoryService.findCategoryByName("Salary", user)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(transactionRequest, user);

        assertNotNull(response);
        assertEquals(new BigDecimal("5000.00"), response.getAmount());
        assertEquals("Salary", response.getCategory());
    }

    @Test
    void createTransaction_FutureDate_ThrowsException() {
        TransactionRequest futureRequest = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date(LocalDate.now().plusDays(1).toString())
                .category("Salary")
                .build();

        assertThrows(InvalidRequestException.class, 
                () -> transactionService.createTransaction(futureRequest, user));
    }

    @Test
    void createTransaction_InvalidDateFormat_ThrowsException() {
        TransactionRequest invalidRequest = TransactionRequest.builder()
                .amount(new BigDecimal("5000.00"))
                .date("01-15-2024")
                .category("Salary")
                .build();

        assertThrows(InvalidRequestException.class, 
                () -> transactionService.createTransaction(invalidRequest, user));
    }

    @Test
    void getAllTransactions_Success() {
        when(transactionRepository.findByUserWithFilters(eq(user), isNull(), isNull(), isNull()))
                .thenReturn(Arrays.asList(transaction));

        TransactionListResponse response = transactionService.getAllTransactions(user, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getTransactions().size());
    }

    @Test
    void getAllTransactions_WithFilters_Success() {
        when(transactionRepository.findByUserWithFilters(eq(user), any(), any(), eq("Salary")))
                .thenReturn(Arrays.asList(transaction));

        TransactionListResponse response = transactionService.getAllTransactions(
                user, "2024-01-01", "2024-01-31", "Salary");

        assertNotNull(response);
        assertEquals(1, response.getTransactions().size());
    }

    @Test
    void getTransaction_Success() {
        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransaction(1L, user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTransaction_NotFound_ThrowsException() {
        when(transactionRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> transactionService.getTransaction(999L, user));
    }

    @Test
    void updateTransaction_Success() {
        TransactionUpdateRequest updateRequest = TransactionUpdateRequest.builder()
                .amount(new BigDecimal("6000.00"))
                .description("Updated salary")
                .build();

        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.updateTransaction(1L, updateRequest, user);

        assertNotNull(response);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithCategory_Success() {
        Category newCategory = Category.builder()
                .name("Bonus")
                .type(TransactionType.INCOME)
                .build();

        TransactionUpdateRequest updateRequest = TransactionUpdateRequest.builder()
                .category("Bonus")
                .build();

        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName("Bonus", user)).thenReturn(newCategory);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.updateTransaction(1L, updateRequest, user);

        assertNotNull(response);
    }

    @Test
    void updateTransaction_NotFound_ThrowsException() {
        TransactionUpdateRequest updateRequest = TransactionUpdateRequest.builder()
                .amount(new BigDecimal("6000.00"))
                .build();

        when(transactionRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> transactionService.updateTransaction(999L, updateRequest, user));
    }

    @Test
    void deleteTransaction_Success() {
        when(transactionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(transaction));

        MessageResponse response = transactionService.deleteTransaction(1L, user);

        assertNotNull(response);
        assertEquals("Transaction deleted successfully", response.getMessage());
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_NotFound_ThrowsException() {
        when(transactionRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> transactionService.deleteTransaction(999L, user));
    }
}

