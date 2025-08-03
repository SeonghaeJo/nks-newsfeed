package com.personal.nksnewfeed.service;

import com.personal.nksnewfeed.dto.PostCreateDto;
import com.personal.nksnewfeed.dto.PostResponseDto;
import com.personal.nksnewfeed.dto.PostUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    
    PostResponseDto createPost(PostCreateDto postCreateDto);
    
    PostResponseDto getPost(Long postId);
    
    PostResponseDto updatePost(Long postId, PostUpdateDto postUpdateDto);
    
    void deletePost(Long postId);
    
    Page<PostResponseDto> getPostsByUserId(Long userId, Pageable pageable);
    
    Page<PostResponseDto> getNewsFeed(Long userId, Pageable pageable);
}