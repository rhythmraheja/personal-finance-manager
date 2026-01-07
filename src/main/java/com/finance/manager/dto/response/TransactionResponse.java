package com.finance.manager.dto.response;

import com.finance.manager.entity.Transaction;
import com.finance.manager.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private String date;
    private String category;
    private String description;
    private TransactionType type;

    public static TransactionResponse fromEntity(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount().setScale(2, RoundingMode.HALF_UP))
                .date(transaction.getDate().toString())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .build();
    }
}

