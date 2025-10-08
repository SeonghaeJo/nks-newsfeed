package com.personal.nksnewfeed.friend.contract;

import com.personal.nksnewfeed.friend.dto.request.FollowRequestDto;
import com.personal.nksnewfeed.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface FriendApiContract {

    @PostMapping("/api/friends/follow")
    ApiResponse<Void> follow(@Valid @RequestBody FollowRequestDto request);
}