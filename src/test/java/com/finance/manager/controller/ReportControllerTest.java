package com.finance.manager.controller;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.User;
import com.finance.manager.exception.GlobalExceptionHandler;
import com.finance.manager.exception.UnauthorizedException;
import com.finance.manager.service.ReportService;
import com.finance.manager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportController reportController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        testUser = User.builder().id(1L).username("test@example.com").build();
    }

    @Test
    void getMonthlyReport_Success() throws Exception {
        MonthlyReportResponse response = MonthlyReportResponse.builder()
                .month(1)
                .year(2024)
                .totalIncome(Map.of("Salary", new BigDecimal("5000.00")))
                .totalExpenses(Map.of("Food", new BigDecimal("500.00"), "Rent", new BigDecimal("1200.00")))
                .netSavings(new BigDecimal("3300.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(reportService.getMonthlyReport(eq(2024), eq(1), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.netSavings").value(3300.00));
    }

    @Test
    void getMonthlyReport_Unauthorized() throws Exception {
        when(userService.getAuthenticatedUser(any()))
                .thenThrow(new UnauthorizedException("Not authenticated"));

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMonthlyReport_EmptyData() throws Exception {
        MonthlyReportResponse response = MonthlyReportResponse.builder()
                .month(6)
                .year(2024)
                .totalIncome(Map.of())
                .totalExpenses(Map.of())
                .netSavings(BigDecimal.ZERO)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(reportService.getMonthlyReport(eq(2024), eq(6), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/reports/monthly/2024/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netSavings").value(0));
    }

    @Test
    void getYearlyReport_Success() throws Exception {
        YearlyReportResponse response = YearlyReportResponse.builder()
                .year(2024)
                .totalIncome(Map.of("Salary", new BigDecimal("60000.00")))
                .totalExpenses(Map.of("Food", new BigDecimal("6000.00"), "Rent", new BigDecimal("14400.00")))
                .netSavings(new BigDecimal("39600.00"))
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(reportService.getYearlyReport(eq(2024), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.netSavings").value(39600.00));
    }

    @Test
    void getYearlyReport_Unauthorized() throws Exception {
        when(userService.getAuthenticatedUser(any()))
                .thenThrow(new UnauthorizedException("Not authenticated"));

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getYearlyReport_EmptyData() throws Exception {
        YearlyReportResponse response = YearlyReportResponse.builder()
                .year(2020)
                .totalIncome(Map.of())
                .totalExpenses(Map.of())
                .netSavings(BigDecimal.ZERO)
                .build();

        when(userService.getAuthenticatedUser(any())).thenReturn(testUser);
        when(reportService.getYearlyReport(eq(2020), any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/reports/yearly/2020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.netSavings").value(0));
    }
}

