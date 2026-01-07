package com.finance.manager.dto.response;

import com.finance.manager.entity.Category;
import com.finance.manager.enums.TransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String name;
    private TransactionType type;
    private boolean custom;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .type(category.getType())
                .custom(category.isCustom())
                .build();
    }
}

