package com.personal.nksnewfeed.service;

public interface PostTransmitService {
    
    void transmitToQueue(Long postId);
}