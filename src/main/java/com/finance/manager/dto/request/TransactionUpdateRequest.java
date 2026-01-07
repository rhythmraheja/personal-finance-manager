package com.finance.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionUpdateRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String category;

    private String description;

    private String date;
}

