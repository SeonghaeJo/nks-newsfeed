package com.personal.nksnewfeed.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    public static String extractToken(final String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new JwtAuthenticationException("Authorization header is missing");
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new JwtAuthenticationException("Invalid authorization header format");
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!StringUtils.hasText(token)) {
            throw new JwtAuthenticationException("Token is missing");
        }

        return token;
    }
}