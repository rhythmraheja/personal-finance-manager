package com.finance.manager.dto.response;

import com.finance.manager.entity.Goal;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponse {

    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private String targetDate;
    private String startDate;
    private BigDecimal currentProgress;
    private BigDecimal progressPercentage;
    private BigDecimal remainingAmount;

    public static GoalResponse fromEntity(Goal goal, BigDecimal currentProgress) {
        BigDecimal targetAmount = goal.getTargetAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal progress = currentProgress.setScale(2, RoundingMode.HALF_UP);

        BigDecimal progressPercentage = BigDecimal.ZERO;
        if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            progressPercentage = progress.multiply(BigDecimal.valueOf(100))
                    .divide(targetAmount, 2, RoundingMode.HALF_UP);
        }

        BigDecimal remaining = targetAmount.subtract(progress);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        return GoalResponse.builder()
                .id(goal.getId())
                .goalName(goal.getGoalName())
                .targetAmount(targetAmount)
                .targetDate(goal.getTargetDate().toString())
                .startDate(goal.getStartDate().toString())
                .currentProgress(formatProgress(progress))
                .progressPercentage(formatPercentage(progressPercentage))
                .remainingAmount(remaining.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private static BigDecimal formatProgress(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal formatPercentage(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("0.0");
        }
        BigDecimal stripped = value.stripTrailingZeros();
        if (stripped.scale() < 1) {
            stripped = stripped.setScale(1);
        }
        return stripped;
    }
}

