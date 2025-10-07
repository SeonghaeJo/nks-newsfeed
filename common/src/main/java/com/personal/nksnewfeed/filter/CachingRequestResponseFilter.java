package com.personal.nksnewfeed.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CachingRequestResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(cachingRequest, cachingResponse);
        } finally {
            cachingResponse.copyBodyToResponse();
        }
    }
}