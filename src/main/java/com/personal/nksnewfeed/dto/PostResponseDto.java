package com.personal.nksnewfeed.dto;

import com.personal.nksnewfeed.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long id,
        Long userId,
        String username,
        String content,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponseDto from(final Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}