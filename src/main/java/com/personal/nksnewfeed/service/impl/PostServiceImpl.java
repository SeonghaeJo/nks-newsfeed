package com.personal.nksnewfeed.service.impl;

import com.personal.nksnewfeed.dto.PostCreateDto;
import com.personal.nksnewfeed.dto.PostResponseDto;
import com.personal.nksnewfeed.dto.PostUpdateDto;
import com.personal.nksnewfeed.entity.Post;
import com.personal.nksnewfeed.entity.User;
import com.personal.nksnewfeed.repository.PostRepository;
import com.personal.nksnewfeed.repository.UserRepository;
import com.personal.nksnewfeed.service.PostService;
import com.personal.nksnewfeed.service.PostTransmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostTransmitService postTransmitService;
    
    @Override
    @Transactional
    @CachePut(value = "posts", key = "#result.id")
    public PostResponseDto createPost(final PostCreateDto postCreateDto) {
        log.info("Creating post for userId: {}", postCreateDto.userId());
        
        final User user = userRepository.findById(postCreateDto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + postCreateDto.userId()));
        
        final Post post = new Post(user, postCreateDto.content(), null);
        final Post savedPost = postRepository.save(post);
        
        log.info("Post created successfully with id: {}", savedPost.getId());
        
        // 포스트를 메시지 큐로 전송 (비동기)
        postTransmitService.transmitToQueue(savedPost.getId());
        
        return PostResponseDto.from(savedPost);
    }
    
    @Override
    @Cacheable(value = "posts", key = "#postId")
    public PostResponseDto getPost(final Long postId) {
        log.debug("Fetching post with id: {}", postId);
        
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        return PostResponseDto.from(post);
    }
    
    @Override
    @Transactional
    @CachePut(value = "posts", key = "#postId")
    public PostResponseDto updatePost(final Long postId, final PostUpdateDto postUpdateDto) {
        log.info("Updating post with id: {}", postId);
        
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        
        post.updateContent(postUpdateDto.content());
        final Post updatedPost = postRepository.save(post);
        
        log.info("Post updated successfully with id: {}", postId);
        
        return PostResponseDto.from(updatedPost);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "posts", key = "#postId")
    public void deletePost(final Long postId) {
        log.info("Deleting post with id: {}", postId);
        
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }
        
        postRepository.deleteById(postId);
        
        log.info("Post deleted successfully with id: {}", postId);
    }
    
    @Override
    @Cacheable(value = "userPosts", key = "#userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PostResponseDto> getPostsByUserId(final Long userId, final Pageable pageable) {
        log.debug("Fetching posts for userId: {}, page: {}", userId, pageable.getPageNumber());
        
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PostResponseDto::from);
    }
}