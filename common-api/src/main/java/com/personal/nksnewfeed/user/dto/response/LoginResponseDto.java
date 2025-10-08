package com.personal.nksnewfeed.user.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        Long userId,
        String username
) {
}