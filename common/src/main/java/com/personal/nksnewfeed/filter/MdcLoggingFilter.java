package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.HeaderConstants;
import com.personal.nksnewfeed.constants.MdcConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String requestId = request.getHeader(HeaderConstants.REQUEST_ID);
            final String traceId = request.getHeader(HeaderConstants.TRACE_ID);
            final String userId = request.getHeader(HeaderConstants.USER_ID);

            if (requestId != null) {
                MDC.put(MdcConstants.REQUEST_ID, requestId);
            }

            if (traceId != null) {
                MDC.put(MdcConstants.TRACE_ID, traceId);
            }

            if (userId != null) {
                MDC.put(MdcConstants.USER_ID, userId);
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}