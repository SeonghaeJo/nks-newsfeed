package com.personal.nksnewfeed.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.nksnewfeed.constants.HeaderConstants;
import com.personal.nksnewfeed.jwt.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        if (ex instanceof MissingHeaderException missingHeaderException) {
            return handleMissingHeaderException(exchange, missingHeaderException);
        }

        if (ex instanceof JwtAuthenticationException jwtAuthenticationException) {
            return handleJwtAuthenticationException(exchange, jwtAuthenticationException);
        }

        return handleGenericException(exchange, ex);
    }

    private Mono<Void> handleMissingHeaderException(final ServerWebExchange exchange, final MissingHeaderException ex) {
        log.warn("Missing required header: {}", ex.getHeaderName());

        final Map<String, Object> errorResponse = createErrorResponse(
                exchange,
                "GATEWAY001",
                ex.getMessage()
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleJwtAuthenticationException(final ServerWebExchange exchange, final JwtAuthenticationException ex) {
        log.warn("JWT authentication failed: {}", ex.getMessage());

        final Map<String, Object> errorResponse = createErrorResponse(
                exchange,
                "GATEWAY002",
                ex.getMessage()
        );

        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, errorResponse);
    }

    private Mono<Void> handleGenericException(final ServerWebExchange exchange, final Throwable ex) {
        log.error("Gateway error: {}", ex.getMessage(), ex);

        final Map<String, Object> errorResponse = createErrorResponse(
                exchange,
                "GATEWAY999",
                "Internal gateway error"
        );

        return writeErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }

    private Map<String, Object> createErrorResponse(final ServerWebExchange exchange, final String code, final String message) {
        final String requestId = exchange.getRequest().getHeaders().getFirst(HeaderConstants.REQUEST_ID);

        final Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        if (requestId != null) {
            errorResponse.put("requestId", requestId);
        }

        return errorResponse;
    }

    private Mono<Void> writeErrorResponse(final ServerWebExchange exchange, final HttpStatus status, final Map<String, Object> errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            final byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            final DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (final JsonProcessingException e) {
            log.error("Failed to serialize error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}