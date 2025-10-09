package com.personal.nksnewfeed.publisher;

import com.personal.nksnewfeed.event.UserCreatedEvent;
import com.personal.nksnewfeed.topology.UserCreatedEventTopology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(final UserCreatedEvent event) {
        log.info("Publishing UserCreatedEvent: userId={}, username={}", event.userId(), event.username());

        rabbitTemplate.convertAndSend(
                UserCreatedEventTopology.EXCHANGE,
                UserCreatedEventTopology.ROUTING_KEY,
                event
        );

        log.info("UserCreatedEvent published successfully: userId={}", event.userId());
    }
}