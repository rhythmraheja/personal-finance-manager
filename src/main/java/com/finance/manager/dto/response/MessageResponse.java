package com.finance.manager.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private String message;

    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }
}

