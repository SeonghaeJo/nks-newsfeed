package com.personal.nksnewfeed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "Username already exists", Level.WARN),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "Email already exists", Level.WARN),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "User not found", Level.WARN),

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "Invalid input", Level.WARN),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "Internal server error", Level.ERROR);

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final Level logLevel;
}