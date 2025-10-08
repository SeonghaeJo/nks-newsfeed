package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.HeaderConstants;
import com.personal.nksnewfeed.constants.MdcConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

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
            final String clientComponent = request.getHeader(HeaderConstants.COMPONENT);

            if (requestId != null) {
                MDC.put(MdcConstants.REQUEST_ID, requestId);
            }

            if (traceId != null) {
                MDC.put(MdcConstants.TRACE_ID, traceId);
            }

            if (userId != null) {
                MDC.put(MdcConstants.USER_ID, userId);
            }

            if (clientComponent != null) {
                MDC.put(MdcConstants.CLIENT_COMPONENT, clientComponent);
            }

            // 현재 컴포넌트 이름 설정
            MDC.put(MdcConstants.COMPONENT, applicationName);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}