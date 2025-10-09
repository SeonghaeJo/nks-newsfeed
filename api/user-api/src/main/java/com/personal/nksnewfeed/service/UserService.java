package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.event.UserCreatedEvent;
import com.personal.nksnewfeed.exception.BusinessException;
import com.personal.nksnewfeed.exception.ErrorCode;
import com.personal.nksnewfeed.jwt.JwtTokenProvider;
import com.personal.nksnewfeed.mapper.UserMapper;
import com.personal.nksnewfeed.publisher.UserCreatedEventPublisher;
import com.personal.nksnewfeed.user.dto.request.LoginRequestDto;
import com.personal.nksnewfeed.user.dto.request.UserRegisterRequestDto;
import com.personal.nksnewfeed.user.dto.response.LoginResponseDto;
import com.personal.nksnewfeed.user.dto.response.UserResponseDto;
import com.personal.nksnewfeed.entity.user.User;
import com.personal.nksnewfeed.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserCreatedEventPublisher userCreatedEventPublisher;

    @Transactional
    public UserResponseDto register(final UserRegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        final String encodedPassword = passwordEncoder.encode(request.password());
        final User user = UserMapper.toEntity(request, encodedPassword);
        final User savedUser = userRepository.save(user);

        final UserCreatedEvent event = new UserCreatedEvent(savedUser.getId(), savedUser.getUsername());
        userCreatedEventPublisher.publish(event);

        return UserMapper.toResponseDto(savedUser);
    }

    public LoginResponseDto login(final LoginRequestDto request) {
        final User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        final String accessToken = jwtTokenProvider.createToken(String.valueOf(user.getId()));
        final String refreshToken = UUID.randomUUID().toString();

        // TODO: cache에 Refresh Token 저장

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername()
        );
    }

    public UserResponseDto getUserById(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserMapper.toResponseDto(user);
    }
}
