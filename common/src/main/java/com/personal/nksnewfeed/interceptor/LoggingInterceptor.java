package com.personal.nksnewfeed.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private static final int MAX_PAYLOAD_LENGTH = 1000;
    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        final String controllerName = handlerMethod.getBeanType().getSimpleName();
        final String methodName = handlerMethod.getMethod().getName();
        final String httpMethod = request.getMethod();
        final String uri = request.getRequestURI();
        final int status = response.getStatus();

        final String requestBody = getRequestBody(request);
        final String responseBody = getResponseBody(response);

        if (ex != null) {
            log.error("API - {} {} -> {}.{} [status={}] [error={}] [request={}] [response={}]",
                    httpMethod, uri, controllerName, methodName, status, ex.getMessage(),
                    truncate(requestBody), truncate(responseBody));
        } else {
            log.info("API - {} {} -> {}.{} [status={}] [request={}] [response={}]",
                    httpMethod, uri, controllerName, methodName, status,
                    truncate(requestBody), truncate(responseBody));
        }
    }

    private String getRequestBody(final HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            final byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private String getResponseBody(final HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            final byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private String truncate(final String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        if (str.length() <= MAX_PAYLOAD_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_PAYLOAD_LENGTH) + "... (truncated)";
    }
}