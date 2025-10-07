package com.personal.nksnewfeed.user.contract;

import com.personal.nksnewfeed.response.ApiResponse;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/users")
public interface UserApiContract {

    @PostMapping("/register")
    ApiResponse<UserResponseDto> register(@Valid @RequestBody UserRegisterRequestDto request);
}