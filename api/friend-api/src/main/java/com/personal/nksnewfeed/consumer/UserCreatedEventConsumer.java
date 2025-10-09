package com.personal.nksnewfeed.consumer;

import com.personal.nksnewfeed.event.UserCreatedEvent;
import com.personal.nksnewfeed.node.UserNode;
import com.personal.nksnewfeed.repository.UserNodeRepository;
import com.personal.nksnewfeed.topology.UserCreatedEventTopology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventConsumer {

    private final UserNodeRepository userNodeRepository;

    @RabbitListener(
            queues = UserCreatedEventTopology.FRIEND_QUEUE,
            errorHandler = "delayedRetryErrorHandler"
    )
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000)
    )
    @Transactional
    public void handleUserCreatedEvent(final UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: userId={}, username={}", event.userId(), event.username());

        // TODO: Remove this test code after retry testing
        if (true) {
            throw new RuntimeException("Intentional error for retry testing");
        }

        if (userNodeRepository.existsByUserId(event.userId())) {
            log.warn("UserNode already exists for userId={}", event.userId());
            return;
        }

        final UserNode userNode = UserNode.builder()
                .userId(event.userId())
                .username(event.username())
                .build();

        userNodeRepository.save(userNode);
        log.info("UserNode created successfully: userId={}, username={}", event.userId(), event.username());
    }
}