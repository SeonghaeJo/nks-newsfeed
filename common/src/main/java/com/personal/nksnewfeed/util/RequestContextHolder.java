package com.personal.nksnewfeed.util;

import com.personal.nksnewfeed.constants.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestContextHolder {

    public static String getRequestId() {
        return getHeader(HeaderConstants.REQUEST_ID);
    }

    public static String getTraceId() {
        return getHeader(HeaderConstants.TRACE_ID);
    }

    public static String getUserId() {
        return getHeader(HeaderConstants.USER_ID);
    }

    private static String getHeader(final String headerName) {
        final ServletRequestAttributes attributes =
                (ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        final HttpServletRequest request = attributes.getRequest();
        return request.getHeader(headerName);
    }
}