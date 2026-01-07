package com.finance.manager.service;

import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.GoalUpdateRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.Goal;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.InvalidRequestException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.GoalRepository;
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
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal;
    private GoalRequest goalRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        goal = Goal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate(LocalDate.of(2027, 1, 1))
                .startDate(LocalDate.of(2024, 1, 1))
                .user(user)
                .build();

        goalRequest = GoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2027-01-01")
                .startDate("2024-01-01")
                .build();
    }

    @Test
    void createGoal_Success() {
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(eq(user), eq(TransactionType.INCOME), any()))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(eq(user), eq(TransactionType.EXPENSE), any()))
                .thenReturn(new BigDecimal("2000.00"));

        GoalResponse response = goalService.createGoal(goalRequest, user);

        assertNotNull(response);
        assertEquals("Emergency Fund", response.getGoalName());
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void createGoal_WithoutStartDate_UsesToday() {
        GoalRequest requestWithoutStart = GoalRequest.builder()
                .goalName("New Goal")
                .targetAmount(new BigDecimal("5000.00"))
                .targetDate("2027-01-01")
                .build();

        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.createGoal(requestWithoutStart, user);

        assertNotNull(response);
    }

    @Test
    void createGoal_PastTargetDate_ThrowsException() {
        GoalRequest invalidRequest = GoalRequest.builder()
                .goalName("Invalid Goal")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2020-01-01")
                .build();

        assertThrows(InvalidRequestException.class, () -> goalService.createGoal(invalidRequest, user));
    }

    @Test
    void createGoal_StartDateAfterTargetDate_ThrowsException() {
        GoalRequest invalidRequest = GoalRequest.builder()
                .goalName("Invalid Goal")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2025-01-01")
                .startDate("2026-01-01")
                .build();

        assertThrows(InvalidRequestException.class, () -> goalService.createGoal(invalidRequest, user));
    }

    @Test
    void getAllGoals_Success() {
        when(goalRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Arrays.asList(goal));
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalListResponse response = goalService.getAllGoals(user);

        assertNotNull(response);
        assertEquals(1, response.getGoals().size());
    }

    @Test
    void getGoal_Success() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.getGoal(1L, user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getGoal_NotFound_ThrowsException() {
        when(goalRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.getGoal(999L, user));
    }

    @Test
    void updateGoal_Success() {
        GoalUpdateRequest updateRequest = GoalUpdateRequest.builder()
                .targetAmount(new BigDecimal("15000.00"))
                .build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.updateGoal(1L, updateRequest, user);

        assertNotNull(response);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void updateGoal_WithTargetDate_Success() {
        GoalUpdateRequest updateRequest = GoalUpdateRequest.builder()
                .targetDate("2028-01-01")
                .build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        GoalResponse response = goalService.updateGoal(1L, updateRequest, user);

        assertNotNull(response);
    }

    @Test
    void updateGoal_PastTargetDate_ThrowsException() {
        GoalUpdateRequest updateRequest = GoalUpdateRequest.builder()
                .targetDate("2020-01-01")
                .build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));

        assertThrows(InvalidRequestException.class, 
                () -> goalService.updateGoal(1L, updateRequest, user));
    }

    @Test
    void updateGoal_NotFound_ThrowsException() {
        GoalUpdateRequest updateRequest = GoalUpdateRequest.builder()
                .targetAmount(new BigDecimal("15000.00"))
                .build();

        when(goalRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> goalService.updateGoal(999L, updateRequest, user));
    }

    @Test
    void deleteGoal_Success() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));

        MessageResponse response = goalService.deleteGoal(1L, user);

        assertNotNull(response);
        assertEquals("Goal deleted successfully", response.getMessage());
        verify(goalRepository).delete(goal);
    }

    @Test
    void deleteGoal_NotFound_ThrowsException() {
        when(goalRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goalService.deleteGoal(999L, user));
    }

    @Test
    void progressCalculation_WithNullValues() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(eq(user), eq(TransactionType.INCOME), any()))
                .thenReturn(null);
        when(transactionRepository.sumAmountByUserAndTypeAndDateAfter(eq(user), eq(TransactionType.EXPENSE), any()))
                .thenReturn(null);

        GoalResponse response = goalService.getGoal(1L, user);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getCurrentProgress());
    }
}

