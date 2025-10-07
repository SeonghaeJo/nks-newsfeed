package com.personal.nksnewfeed.exception.handler;

import com.personal.nksnewfeed.constants.ExceptionHandlerOrder;
import com.personal.nksnewfeed.exception.ErrorCode;
import com.personal.nksnewfeed.exception.ErrorResponse;
import com.personal.nksnewfeed.util.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(ExceptionHandlerOrder.METHOD_ARGUMENT_NOT_VALID_EXCEPTION)
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        log.atLevel(errorCode.getLogLevel())
                .setCause(e)
                .log("MethodArgumentNotValidException: {}", e.getMessage());

        final String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(errorCode.getMessage());

        final String requestId = RequestContextHolder.getRequestId();
        final ErrorResponse response = ErrorResponse.of(errorCode, message, requestId);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
