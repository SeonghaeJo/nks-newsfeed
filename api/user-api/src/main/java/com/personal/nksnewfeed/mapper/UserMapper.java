package com.personal.nksnewfeed.mapper;

import com.personal.nksnewfeed.entity.user.User;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toEntity(final UserRegisterRequestDto request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public static UserResponseDto toResponseDto(final User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}