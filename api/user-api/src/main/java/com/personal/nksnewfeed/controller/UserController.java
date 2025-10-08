package com.personal.nksnewfeed.controller;

import com.personal.nksnewfeed.response.ApiResponse;
import com.personal.nksnewfeed.response.ApiResponseUtils;
import com.personal.nksnewfeed.user.contract.UserApiContract;
import com.personal.nksnewfeed.user.dto.request.LoginRequestDto;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.LoginResponseDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import com.personal.nksnewfeed.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApiContract {

    private final UserService userService;

    @Override
    public ApiResponse<UserResponseDto> register(final UserRegisterRequestDto request) {
        final UserResponseDto user = userService.register(request);
        return ApiResponseUtils.success(user);
    }

    @Override
    public ApiResponse<LoginResponseDto> login(final LoginRequestDto request) {
        final LoginResponseDto loginResponse = userService.login(request);
        return ApiResponseUtils.success(loginResponse);
    }

    @Override
    public ApiResponse<UserResponseDto> getUserById(final Long userId) {
        final UserResponseDto user = userService.getUserById(userId);
        return ApiResponseUtils.success(user);
    }
}