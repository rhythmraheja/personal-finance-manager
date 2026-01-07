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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public GoalResponse createGoal(GoalRequest request, User user) {
        LocalDate targetDate = parseDate(request.getTargetDate());
        LocalDate startDate = request.getStartDate() != null 
                ? parseDate(request.getStartDate()) 
                : LocalDate.now();

        if (targetDate.isBefore(LocalDate.now()) || targetDate.isEqual(LocalDate.now())) {
            throw new InvalidRequestException("Target date must be in the future");
        }

        if (startDate.isAfter(targetDate)) {
            throw new InvalidRequestException("Start date cannot be after target date");
        }

        Goal goal = Goal.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .targetDate(targetDate)
                .startDate(startDate)
                .user(user)
                .build();

        Goal saved = goalRepository.save(goal);
        log.info("Goal created: {} for user: {}", saved.getId(), user.getUsername());

        BigDecimal progress = calculateProgress(user, startDate);
        return GoalResponse.fromEntity(saved, progress);
    }

    @Transactional(readOnly = true)
    public GoalListResponse getAllGoals(User user) {
        List<Goal> goals = goalRepository.findByUserOrderByCreatedAtDesc(user);

        List<GoalResponse> responses = goals.stream()
                .map(goal -> {
                    BigDecimal progress = calculateProgress(user, goal.getStartDate());
                    return GoalResponse.fromEntity(goal, progress);
                })
                .collect(Collectors.toList());

        return GoalListResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long id, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        BigDecimal progress = calculateProgress(user, goal.getStartDate());
        return GoalResponse.fromEntity(goal, progress);
    }

    @Transactional
    public GoalResponse updateGoal(Long id, GoalUpdateRequest request, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            LocalDate targetDate = parseDate(request.getTargetDate());
            if (targetDate.isBefore(LocalDate.now()) || targetDate.isEqual(LocalDate.now())) {
                throw new InvalidRequestException("Target date must be in the future");
            }
            goal.setTargetDate(targetDate);
        }

        Goal saved = goalRepository.save(goal);
        log.info("Goal updated: {} for user: {}", saved.getId(), user.getUsername());

        BigDecimal progress = calculateProgress(user, goal.getStartDate());
        return GoalResponse.fromEntity(saved, progress);
    }

    @Transactional
    public MessageResponse deleteGoal(Long id, User user) {
        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        goalRepository.delete(goal);
        log.info("Goal deleted: {} for user: {}", id, user.getUsername());

        return MessageResponse.of("Goal deleted successfully");
    }

    private BigDecimal calculateProgress(User user, LocalDate startDate) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateAfter(
                user, TransactionType.INCOME, startDate);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserAndTypeAndDateAfter(
                user, TransactionType.EXPENSE, startDate);

        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal progress = totalIncome.subtract(totalExpenses);
        return progress.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : progress;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestException("Invalid date format. Use YYYY-MM-DD");
        }
    }
}

