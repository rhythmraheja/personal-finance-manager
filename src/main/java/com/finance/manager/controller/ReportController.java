package com.finance.manager.controller;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.User;
import com.finance.manager.service.ReportService;
import com.finance.manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @PathVariable int year,
            HttpSession session) {
        User user = userService.getAuthenticatedUser(session);
        YearlyReportResponse response = reportService.getYearlyReport(year, user);
        return ResponseEntity.ok(response);
    }
}

