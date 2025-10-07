package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.exception.BusinessException;
import com.personal.nksnewfeed.exception.ErrorCode;
import com.personal.nksnewfeed.mapper.UserMapper;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import com.personal.nksnewfeed.entity.user.User;
import com.personal.nksnewfeed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto register(final UserRegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        final User user = UserMapper.toEntity(request);
        final User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }
}
