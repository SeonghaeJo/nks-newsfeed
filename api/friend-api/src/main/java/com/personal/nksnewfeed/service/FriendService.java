package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.exception.BusinessException;
import com.personal.nksnewfeed.exception.ErrorCode;
import com.personal.nksnewfeed.friend.dto.request.FollowRequestDto;
import com.personal.nksnewfeed.response.ApiResponse;
import com.personal.nksnewfeed.user.client.UserFeignClient;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserFeignClient userFeignClient;

    public void follow(final FollowRequestDto request) {
        final ApiResponse<UserResponseDto> response = userFeignClient.getUserById(request.targetUserId());

        if (response.data() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("User validation successful for userId: {}", request.targetUserId());

        // TODO: Neo4j FOLLOWS 관계 생성
        // TODO: cache 팔로워/팔로잉 캐시 무효화
        // TODO: messaging FollowCreatedEvent 발행
    }
}