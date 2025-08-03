package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.dto.UserCreateDto;
import com.personal.nksnewfeed.dto.UserResponseDto;
import com.personal.nksnewfeed.dto.UserUpdateDto;

public interface UserService {
    
    UserResponseDto createUser(UserCreateDto userCreateDto);
    
    UserResponseDto getUser(Long userId);
    
    UserResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto);
    
    void deleteUser(Long userId);
}