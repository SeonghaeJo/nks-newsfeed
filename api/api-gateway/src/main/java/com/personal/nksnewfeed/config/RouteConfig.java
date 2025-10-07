package com.personal.nksnewfeed.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(final RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", r -> r
                        .path("/api/users/**")
                        .uri("http://localhost:8081"))
                .route("post-api", r -> r
                        .path("/api/posts/**")
                        .uri("http://localhost:8082"))
                .route("post-fanout-api", r -> r
                        .path("/api/fanout/**")
                        .uri("http://localhost:8083"))
                .route("newsfeed-api", r -> r
                        .path("/api/newsfeed/**")
                        .uri("http://localhost:8084"))
                .route("friend-api", r -> r
                        .path("/api/friends/**")
                        .uri("http://localhost:8085"))
                .build();
    }
}