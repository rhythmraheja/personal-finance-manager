package com.finance.manager.service;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.InvalidRequestException;
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
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;
    private Transaction incomeTransaction;
    private Transaction expenseTransaction;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        incomeTransaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("5000.00"))
                .date(LocalDate.of(2024, 1, 15))
                .category("Salary")
                .type(TransactionType.INCOME)
                .user(user)
                .build();

        expenseTransaction = Transaction.builder()
                .id(2L)
                .amount(new BigDecimal("1500.00"))
                .date(LocalDate.of(2024, 1, 20))
                .category("Rent")
                .type(TransactionType.EXPENSE)
                .user(user)
                .build();
    }

    @Test
    void getMonthlyReport_Success() {
        when(transactionRepository.findByUserAndMonth(user, 2024, 1))
                .thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 1, user);

        assertNotNull(response);
        assertEquals(1, response.getMonth());
        assertEquals(2024, response.getYear());
        assertEquals(new BigDecimal("5000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1500.00"), response.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("3500.00"), response.getNetSavings());
    }

    @Test
    void getMonthlyReport_NoTransactions_ReturnsZero() {
        when(transactionRepository.findByUserAndMonth(user, 2024, 12))
                .thenReturn(Collections.emptyList());

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 12, user);

        assertNotNull(response);
        assertEquals(12, response.getMonth());
        assertEquals(2024, response.getYear());
        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getNetSavings());
    }

    @Test
    void getMonthlyReport_InvalidMonth_ThrowsException() {
        assertThrows(InvalidRequestException.class, 
                () -> reportService.getMonthlyReport(2024, 13, user));
    }

    @Test
    void getMonthlyReport_MonthZero_ThrowsException() {
        assertThrows(InvalidRequestException.class, 
                () -> reportService.getMonthlyReport(2024, 0, user));
    }

    @Test
    void getMonthlyReport_NegativeMonth_ThrowsException() {
        assertThrows(InvalidRequestException.class, 
                () -> reportService.getMonthlyReport(2024, -1, user));
    }

    @Test
    void getYearlyReport_Success() {
        when(transactionRepository.findByUserAndYear(user, 2024))
                .thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));

        YearlyReportResponse response = reportService.getYearlyReport(2024, user);

        assertNotNull(response);
        assertEquals(2024, response.getYear());
        assertEquals(new BigDecimal("5000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1500.00"), response.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("3500.00"), response.getNetSavings());
    }

    @Test
    void getYearlyReport_NoTransactions_ReturnsZero() {
        when(transactionRepository.findByUserAndYear(user, 2023))
                .thenReturn(Collections.emptyList());

        YearlyReportResponse response = reportService.getYearlyReport(2023, user);

        assertNotNull(response);
        assertEquals(2023, response.getYear());
        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getNetSavings());
    }

    @Test
    void getMonthlyReport_MultipleTransactionsSameCategory() {
        Transaction anotherIncome = Transaction.builder()
                .id(3L)
                .amount(new BigDecimal("2000.00"))
                .date(LocalDate.of(2024, 1, 25))
                .category("Salary")
                .type(TransactionType.INCOME)
                .user(user)
                .build();

        when(transactionRepository.findByUserAndMonth(user, 2024, 1))
                .thenReturn(Arrays.asList(incomeTransaction, anotherIncome, expenseTransaction));

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 1, user);

        assertNotNull(response);
        assertEquals(new BigDecimal("7000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("5500.00"), response.getNetSavings());
    }

    @Test
    void getYearlyReport_MultipleCategories() {
        Transaction foodExpense = Transaction.builder()
                .id(3L)
                .amount(new BigDecimal("500.00"))
                .date(LocalDate.of(2024, 1, 25))
                .category("Food")
                .type(TransactionType.EXPENSE)
                .user(user)
                .build();

        when(transactionRepository.findByUserAndYear(user, 2024))
                .thenReturn(Arrays.asList(incomeTransaction, expenseTransaction, foodExpense));

        YearlyReportResponse response = reportService.getYearlyReport(2024, user);

        assertNotNull(response);
        assertEquals(new BigDecimal("5000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("1500.00"), response.getTotalExpenses().get("Rent"));
        assertEquals(new BigDecimal("500.00"), response.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("3000.00"), response.getNetSavings());
    }
}

