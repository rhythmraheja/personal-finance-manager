package com.finance.manager.service;

import com.finance.manager.dto.request.TransactionRequest;
import com.finance.manager.dto.request.TransactionUpdateRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.TransactionListResponse;
import com.finance.manager.dto.response.TransactionResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.InvalidRequestException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        LocalDate date = parseAndValidateDate(request.getDate());

        if (date.isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Transaction date cannot be in the future");
        }

        Category category = categoryService.findCategoryByName(request.getCategory(), user);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(date)
                .category(category.getName())
                .type(category.getType())
                .description(request.getDescription())
                .user(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: {} for user: {}", saved.getId(), user.getUsername());

        return TransactionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public TransactionListResponse getAllTransactions(User user, String startDate, String endDate, String category) {
        LocalDate start = startDate != null ? parseDate(startDate) : null;
        LocalDate end = endDate != null ? parseDate(endDate) : null;

        List<Transaction> transactions = transactionRepository.findByUserWithFilters(user, start, end, category);

        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());

        return TransactionListResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        return TransactionResponse.fromEntity(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getCategory() != null) {
            Category category = categoryService.findCategoryByName(request.getCategory(), user);
            transaction.setCategory(category.getName());
            transaction.setType(category.getType());
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction updated: {} for user: {}", saved.getId(), user.getUsername());

        return TransactionResponse.fromEntity(saved);
    }

    @Transactional
    public MessageResponse deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        transactionRepository.delete(transaction);
        log.info("Transaction deleted: {} for user: {}", id, user.getUsername());

        return MessageResponse.of("Transaction deleted successfully");
    }

    private LocalDate parseAndValidateDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("Invalid date format. Use YYYY-MM-DD");
        }
    }
}

