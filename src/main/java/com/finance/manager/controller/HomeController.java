package com.finance.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(getApiInfo());
    }

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> api() {
        return ResponseEntity.ok(getApiInfo());
    }

    private Map<String, Object> getApiInfo() {
        return Map.of(
            "application", "Personal Finance Manager API",
            "version", "1.0.0",
            "status", "running",
            "endpoints", Map.of(
                "auth", Map.of(
                    "register", "POST /api/auth/register",
                    "login", "POST /api/auth/login",
                    "logout", "POST /api/auth/logout"
                ),
                "transactions", Map.of(
                    "create", "POST /api/transactions",
                    "getAll", "GET /api/transactions",
                    "getById", "GET /api/transactions/{id}",
                    "update", "PUT /api/transactions/{id}",
                    "delete", "DELETE /api/transactions/{id}"
                ),
                "categories", Map.of(
                    "getAll", "GET /api/categories",
                    "create", "POST /api/categories",
                    "delete", "DELETE /api/categories/{name}"
                ),
                "goals", Map.of(
                    "create", "POST /api/goals",
                    "getAll", "GET /api/goals",
                    "getById", "GET /api/goals/{id}",
                    "update", "PUT /api/goals/{id}",
                    "delete", "DELETE /api/goals/{id}"
                ),
                "reports", Map.of(
                    "monthly", "GET /api/reports/monthly/{year}/{month}",
                    "yearly", "GET /api/reports/yearly/{year}"
                )
            )
        );
    }
}

