package com.finance.manager.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private String message;
    private Long userId;

    public static RegisterResponse success(Long userId) {
        return RegisterResponse.builder()
                .message("User registered successfully")
                .userId(userId)
                .build();
    }
}

