package com.personal.nksnewfeed.controller;

import com.personal.nksnewfeed.friend.contract.FriendApiContract;
import com.personal.nksnewfeed.friend.dto.request.FollowRequestDto;
import com.personal.nksnewfeed.response.ApiResponse;
import com.personal.nksnewfeed.response.ApiResponseUtils;
import com.personal.nksnewfeed.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController implements FriendApiContract {

    private final FriendService friendService;

    @Override
    public ApiResponse<Void> follow(final FollowRequestDto request) {
        friendService.follow(request);
        return ApiResponseUtils.success(null);
    }
}