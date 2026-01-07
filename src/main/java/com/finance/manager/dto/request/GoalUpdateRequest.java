package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUpdateRequest {

    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Target date must be in YYYY-MM-DD format")
    private String targetDate;
}

