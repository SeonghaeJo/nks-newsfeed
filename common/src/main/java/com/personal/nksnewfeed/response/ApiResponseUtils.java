package com.personal.nksnewfeed.response;

import com.personal.nksnewfeed.util.RequestContextHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseUtils {

    public static <T> ApiResponse<T> success(final T data) {
        final String requestId = RequestContextHolder.getRequestId();
        return ApiResponse.of(data, requestId);
    }

    public static <T> ApiResponse<List<T>> successList(final List<T> items) {
        final String requestId = RequestContextHolder.getRequestId();
        return ApiResponse.ofList(items, requestId);
    }

    public static <T> ApiResponse<List<T>> successPage(final Page<T> page) {
        final String requestId = RequestContextHolder.getRequestId();
        return ApiResponse.ofPage(page, requestId);
    }

    public static <T> ApiResponse<T> empty() {
        final String requestId = RequestContextHolder.getRequestId();
        return ApiResponse.empty(requestId);
    }
}