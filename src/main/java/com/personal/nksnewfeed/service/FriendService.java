package com.personal.nksnewfeed.service;

import java.util.List;

public interface FriendService {
    
    void followUser(Long userId, Long targetUserId);
    
    void unfollowUser(Long userId, Long targetUserId);
    
    List<Long> getFollowers(Long userId);
    
    List<Long> getFollowing(Long userId);
    
    boolean isFollowing(Long userId, Long targetUserId);
}