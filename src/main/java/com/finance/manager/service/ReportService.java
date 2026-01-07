package com.finance.manager.service;

import com.finance.manager.dto.response.MonthlyReportResponse;
import com.finance.manager.dto.response.YearlyReportResponse;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.enums.TransactionType;
import com.finance.manager.exception.InvalidRequestException;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(int year, int month, User user) {
        if (month < 1 || month > 12) {
            throw new InvalidRequestException("Month must be between 1 and 12");
        }

        List<Transaction> transactions = transactionRepository.findByUserAndMonth(user, year, month);

        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal netIncome = BigDecimal.ZERO;
        BigDecimal netExpenses = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);
            String category = transaction.getCategory();

            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome.merge(category, amount, BigDecimal::add);
                netIncome = netIncome.add(amount);
            } else {
                totalExpenses.merge(category, amount, BigDecimal::add);
                netExpenses = netExpenses.add(amount);
            }
        }

        BigDecimal netSavings = formatAmount(netIncome.subtract(netExpenses));

        return MonthlyReportResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .build();
    }

    @Transactional(readOnly = true)
    public YearlyReportResponse getYearlyReport(int year, User user) {
        List<Transaction> transactions = transactionRepository.findByUserAndYear(user, year);

        Map<String, BigDecimal> totalIncome = new HashMap<>();
        Map<String, BigDecimal> totalExpenses = new HashMap<>();
        BigDecimal netIncome = BigDecimal.ZERO;
        BigDecimal netExpenses = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);
            String category = transaction.getCategory();

            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome.merge(category, amount, BigDecimal::add);
                netIncome = netIncome.add(amount);
            } else {
                totalExpenses.merge(category, amount, BigDecimal::add);
                netExpenses = netExpenses.add(amount);
            }
        }

        BigDecimal netSavings = formatAmount(netIncome.subtract(netExpenses));

        return YearlyReportResponse.builder()
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .build();
    }

    private BigDecimal formatAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}

