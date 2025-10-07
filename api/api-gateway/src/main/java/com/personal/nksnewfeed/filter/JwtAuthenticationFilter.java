package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.AuthConstants;
import com.personal.nksnewfeed.constants.FilterOrder;
import com.personal.nksnewfeed.constants.HeaderConstants;
import com.personal.nksnewfeed.jwt.JwtAuthenticationException;
import com.personal.nksnewfeed.jwt.JwtTokenExtractor;
import com.personal.nksnewfeed.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String path = request.getPath().value();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        final String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        try {
            final String token = JwtTokenExtractor.extractToken(authorizationHeader);

            if (!jwtTokenProvider.validateToken(token)) {
                return Mono.error(new JwtAuthenticationException("Invalid JWT token"));
            }

            final String userId = jwtTokenProvider.getUserId(token);

            final ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder.header(HeaderConstants.USER_ID, userId))
                    .build();

            return chain.filter(modifiedExchange);

        } catch (final JwtAuthenticationException e) {
            log.warn("JWT authentication failed for path {}: {}", path, e.getMessage());
            return Mono.error(e);
        }
    }

    private boolean isPublicPath(final String path) {
        return AuthConstants.PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return FilterOrder.JWT_AUTHENTICATION_FILTER;
    }
}
