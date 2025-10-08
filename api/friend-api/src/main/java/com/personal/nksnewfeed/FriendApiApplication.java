package com.personal.nksnewfeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FriendApiApplication {
    public static void main(final String[] args) {
        SpringApplication.run(FriendApiApplication.class, args);
    }
}