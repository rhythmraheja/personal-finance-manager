package com.finance.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
            "application", "Personal Finance Manager API",
            "version", "1.0.0",
            "status", "running",
            "documentation", Map.of(
                "auth", "/api/auth/register, /api/auth/login, /api/auth/logout",
                "transactions", "/api/transactions",
                "categories", "/api/categories",
                "goals", "/api/goals",
                "reports", "/api/reports/monthly/{year}/{month}, /api/reports/yearly/{year}"
            )
        ));
    }
}

