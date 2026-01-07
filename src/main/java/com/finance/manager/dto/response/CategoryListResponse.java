package com.finance.manager.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListResponse {

    private List<CategoryResponse> categories;

    public static CategoryListResponse of(List<CategoryResponse> categories) {
        return new CategoryListResponse(categories);
    }
}

