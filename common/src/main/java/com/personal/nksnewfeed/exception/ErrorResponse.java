package com.personal.nksnewfeed.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        String code,
        String message,
        String requestId,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(final ErrorCode errorCode, final String requestId) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(final ErrorCode errorCode, final String message, final String requestId) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}