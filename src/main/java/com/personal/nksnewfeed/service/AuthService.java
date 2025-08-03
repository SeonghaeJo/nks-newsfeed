package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.dto.LoginRequestDto;
import com.personal.nksnewfeed.dto.TokenResponseDto;

public interface AuthService {
    
    TokenResponseDto login(LoginRequestDto loginRequestDto);
    
    void logout(String token);
    
    TokenResponseDto refreshToken(String refreshToken);
    
    boolean validateToken(String token);
}