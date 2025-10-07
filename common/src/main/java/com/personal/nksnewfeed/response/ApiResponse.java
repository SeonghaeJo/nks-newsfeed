package com.personal.nksnewfeed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String requestId,
        LocalDateTime timestamp,
        T data,
        PageInfo pageInfo,
        Integer responseCount
) {
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageInfo(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
    }

    public static <T> ApiResponse<T> of(final T data, final String requestId) {
        return ApiResponse.<T>builder()
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<List<T>> ofList(final List<T> items, final String requestId) {
        return ApiResponse.<List<T>>builder()
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .data(items)
                .responseCount(items.size())
                .build();
    }

    public static <T> ApiResponse<List<T>> ofPage(final Page<T> page, final String requestId) {
        final PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();

        return ApiResponse.<List<T>>builder()
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .data(page.getContent())
                .pageInfo(pageInfo)
                .responseCount(page.getContent().size())
                .build();
    }

    public static <T> ApiResponse<T> empty(final String requestId) {
        return ApiResponse.<T>builder()
                .requestId(requestId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}