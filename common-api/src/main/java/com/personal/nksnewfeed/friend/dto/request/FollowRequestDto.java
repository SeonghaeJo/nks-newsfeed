package com.personal.nksnewfeed.friend.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull
        Long targetUserId
) {
}