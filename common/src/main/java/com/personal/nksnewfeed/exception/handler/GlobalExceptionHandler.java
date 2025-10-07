package com.personal.nksnewfeed.exception.handler;

import com.personal.nksnewfeed.constants.ExceptionHandlerOrder;
import com.personal.nksnewfeed.exception.ErrorCode;
import com.personal.nksnewfeed.exception.ErrorResponse;
import com.personal.nksnewfeed.util.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(ExceptionHandlerOrder.GLOBAL_EXCEPTION)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.atLevel(errorCode.getLogLevel())
                .setCause(e)
                .log("Exception: {}", e.getMessage());

        final String requestId = RequestContextHolder.getRequestId();
        final ErrorResponse response = ErrorResponse.of(errorCode, requestId);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}