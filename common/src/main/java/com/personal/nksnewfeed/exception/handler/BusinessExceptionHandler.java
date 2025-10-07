package com.personal.nksnewfeed.exception.handler;

import com.personal.nksnewfeed.constants.ExceptionHandlerOrder;
import com.personal.nksnewfeed.exception.BusinessException;
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
@Order(ExceptionHandlerOrder.BUSINESS_EXCEPTION)
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.atLevel(errorCode.getLogLevel())
                .setCause(e)
                .log("BusinessException: {}", e.getMessage());

        final String requestId = RequestContextHolder.getRequestId();
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage(), requestId);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}