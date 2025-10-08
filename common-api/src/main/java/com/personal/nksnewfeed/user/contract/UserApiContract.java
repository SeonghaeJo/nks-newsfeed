package com.personal.nksnewfeed.user.contract;

import com.personal.nksnewfeed.response.ApiResponse;
import com.personal.nksnewfeed.user.dto.request.LoginRequestDto;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.LoginResponseDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserApiContract {

    @PostMapping("/api/users/register")
    ApiResponse<UserResponseDto> register(@Valid @RequestBody UserRegisterRequestDto request);

    @PostMapping("/api/users/login")
    ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request);

    @GetMapping("/api/users/{userId}")
    ApiResponse<UserResponseDto> getUserById(@PathVariable("userId") Long userId);
}