package com.personal.nksnewfeed.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.personal.nksnewfeed.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        final Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        final long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        try {
            final Map<String, Object> httpData = buildHttpData(request, response);
            final String message = String.format("HTTP %s %s -> %d (%d ms)",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);

            if (ex != null) {
                final Level logLevel = getLogLevel(ex);
                log.atLevel(logLevel)
                        .addMarker(Markers.append("http", httpData))
                        .setCause(ex)
                        .log(message);
            } else {
                log.info(Markers.append("http", httpData), message);
            }
        } catch (final Exception e) {
            log.error("Failed to create structured log", e);
        }
    }

    private Level getLogLevel(final Exception ex) {
        if (ex instanceof BusinessException businessException) {
            return businessException.getErrorCode().getLogLevel();
        }
        return Level.ERROR;
    }

    private Map<String, Object> buildHttpData(final HttpServletRequest request, final HttpServletResponse response) {
        final Map<String, Object> httpMap = new HashMap<>();
        httpMap.put("method", request.getMethod());
        httpMap.put("path", request.getRequestURI());
        httpMap.put("statusCode", response.getStatus());

        // request
        final Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("headers", getRequestHeadersAsString(request));
        requestMap.put("body", maskSensitiveData(getRequestBody(request)));
        httpMap.put("request", requestMap);

        // response
        final Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("body", getResponseBody(response));
        httpMap.put("response", responseMap);

        return httpMap;
    }

    private String getRequestHeadersAsString(final HttpServletRequest request) {
        final Map<String, String> headers = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(name -> name, request::getHeader));

        try {
            return objectMapper.writeValueAsString(headers);
        } catch (final Exception e) {
            return headers.toString();
        }
    }

    private String getRequestBody(final HttpServletRequest request) {
        final ContentCachingRequestWrapper wrapper = unwrapRequest(request);
        if (wrapper != null) {
            final byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private String getResponseBody(final HttpServletResponse response) {
        final ContentCachingResponseWrapper wrapper = unwrapResponse(response);
        if (wrapper != null) {
            final byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private ContentCachingRequestWrapper unwrapRequest(final HttpServletRequest request) {
        return WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    }

    private ContentCachingResponseWrapper unwrapResponse(final HttpServletResponse response) {
        return WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
    }

    private String maskSensitiveData(final String body) {
        if (body == null || body.isEmpty()) {
            return "";
        }

        try {
            final JsonNode jsonNode = objectMapper.readTree(body);
            if (jsonNode.isObject()) {
                final ObjectNode objectNode = (ObjectNode) jsonNode;
                if (objectNode.has("password")) {
                    objectNode.put("password", "***");
                }
                return objectMapper.writeValueAsString(objectNode);
            }
            return body;
        } catch (final Exception e) {
            return body;
        }
    }
}