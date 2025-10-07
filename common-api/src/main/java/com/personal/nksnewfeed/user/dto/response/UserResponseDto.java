package com.personal.nksnewfeed.user.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email,
        String bio,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}