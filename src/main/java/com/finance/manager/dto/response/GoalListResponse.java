package com.finance.manager.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalListResponse {

    private List<GoalResponse> goals;

    public static GoalListResponse of(List<GoalResponse> goals) {
        return new GoalListResponse(goals);
    }
}

