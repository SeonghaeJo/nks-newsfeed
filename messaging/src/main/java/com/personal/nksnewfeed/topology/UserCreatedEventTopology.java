package com.personal.nksnewfeed.topology;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserCreatedEventTopology {

    // Exchange
    public static final String EXCHANGE = "ex.user.created.v1";
    public static final String ROUTING_KEY = "user.created";

    // Friend Consumer
    public static final String FRIEND_QUEUE = "q.friend.user.created.v1";
    public static final String FRIEND_DLX = "dlx.friend.user.created.v1";
    public static final String FRIEND_DLQ = "dlq.friend.user.created.v1";

    // Main Exchange (delayed type for retry with x-delay header)
    @Bean
    public CustomExchange userCreatedExchange() {
        return new CustomExchange(
                EXCHANGE, 
                "x-delayed-message", 
                true, 
                false,
                Map.of("x-delayed-type", "direct")
        );
    }

    // Friend Consumer - Main Queue
    @Bean
    public Queue friendUserCreatedQueue() {
        return QueueBuilder.durable(FRIEND_QUEUE)
                .withArgument("x-dead-letter-exchange", FRIEND_DLX)
                .withArgument("x-dead-letter-routing-key", FRIEND_DLQ)
                .build();
    }

    @Bean
    public Binding friendUserCreatedBinding(
            @Qualifier("friendUserCreatedQueue") final Queue friendUserCreatedQueue,
            @Qualifier("userCreatedExchange") final CustomExchange userCreatedExchange
    ) {
        return BindingBuilder.bind(friendUserCreatedQueue)
                .to(userCreatedExchange)
                .with(ROUTING_KEY)
                .noargs();
    }

    // Friend Consumer - DLX
    @Bean
    public DirectExchange friendUserCreatedDlx() {
        return new DirectExchange(FRIEND_DLX, true, false);
    }

    // Friend Consumer - DLQ
    @Bean
    public Queue friendUserCreatedDlq() {
        return QueueBuilder.durable(FRIEND_DLQ).build();
    }

    @Bean
    public Binding friendUserCreatedDlqBinding(
            @Qualifier("friendUserCreatedDlq") final Queue friendUserCreatedDlq,
            @Qualifier("friendUserCreatedDlx") final DirectExchange friendUserCreatedDlx
    ) {
        return BindingBuilder.bind(friendUserCreatedDlq)
                .to(friendUserCreatedDlx)
                .with(FRIEND_DLQ);
    }
}