package com.finance.manager.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionListResponse {

    private List<TransactionResponse> transactions;

    public static TransactionListResponse of(List<TransactionResponse> transactions) {
        return new TransactionListResponse(transactions);
    }
}

