package com.personal.nksnewfeed.jwt;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(final String message) {
        super(message);
    }

    public JwtAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}