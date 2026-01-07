package com.finance.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.manager.dto.request.GoalRequest;
import com.finance.manager.dto.request.GoalUpdateRequest;
import com.finance.manager.dto.response.GoalListResponse;
import com.finance.manager.dto.response.GoalResponse;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.entity.User;
import com.finance.manager.exception.*;
import com.finance.manager.service.GoalService;
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
class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GoalController goalController;

    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(goalController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).username("test@example.com").build();
    }

    @Test
    void createGoal_Success() throws Exception {
        GoalRequest request = GoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2026-01-01")
                .startDate("2025-01-01")
                .build();

        GoalResponse response = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2026-01-01")
                .startDate("2025-01-01")
                .currentProgress(BigDecimal.ZERO)
                .progressPercentage(BigDecimal.ZERO)
                .remainingAmount(new BigDecimal("10000.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.createGoal(any(GoalRequest.class), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    void createGoal_Unauthorized() throws Exception {
        GoalRequest request = GoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .targetDate("2026-01-01")
                .build();

        when(userService.getAuthenticatedUser(any()))
                .thenThrow(new UnauthorizedException("Not authenticated"));

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllGoals_Success() throws Exception {
        GoalResponse goal = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .currentProgress(new BigDecimal("2000.00"))
                .progressPercentage(new BigDecimal("20.00"))
                .remainingAmount(new BigDecimal("8000.00"))
                .build();

        GoalListResponse response = GoalListResponse.builder()
                .goals(List.of(goal))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.getAllGoals(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals").isArray())
                .andExpect(jsonPath("$.goals[0].id").value(1));
    }

    @Test
    void getGoal_Success() throws Exception {
        GoalResponse response = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("10000.00"))
                .currentProgress(new BigDecimal("2000.00"))
                .progressPercentage(new BigDecimal("20.00"))
                .remainingAmount(new BigDecimal("8000.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.getGoal(eq(1L), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    void getGoal_NotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.getGoal(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Goal", "id", 999L));

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGoal_Forbidden() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.getGoal(eq(2L), any(User.class)))
                .thenThrow(new ForbiddenException("Cannot access another user's goal"));

        mockMvc.perform(get("/api/goals/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateGoal_Success() throws Exception {
        GoalUpdateRequest request = GoalUpdateRequest.builder()
                .targetAmount(new BigDecimal("15000.00"))
                .targetDate("2027-01-01")
                .build();

        GoalResponse response = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("15000.00"))
                .targetDate("2027-01-01")
                .currentProgress(new BigDecimal("2000.00"))
                .progressPercentage(new BigDecimal("13.33"))
                .remainingAmount(new BigDecimal("13000.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.updateGoal(eq(1L), any(GoalUpdateRequest.class), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(15000.00));
    }

    @Test
    void updateGoal_NotFound() throws Exception {
        GoalUpdateRequest request = GoalUpdateRequest.builder()
                .targetAmount(new BigDecimal("15000.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.updateGoal(eq(999L), any(GoalUpdateRequest.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Goal", "id", 999L));

        mockMvc.perform(put("/api/goals/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGoal_Success() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.deleteGoal(eq(1L), any(User.class)))
                .thenReturn(MessageResponse.of("Goal deleted successfully"));

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }

    @Test
    void deleteGoal_NotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.deleteGoal(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Goal", "id", 999L));

        mockMvc.perform(delete("/api/goals/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGoal_Forbidden() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(goalService.deleteGoal(eq(2L), any(User.class)))
                .thenThrow(new ForbiddenException("Cannot delete another user's goal"));

        mockMvc.perform(delete("/api/goals/2"))
                .andExpect(status().isForbidden());
    }
}
