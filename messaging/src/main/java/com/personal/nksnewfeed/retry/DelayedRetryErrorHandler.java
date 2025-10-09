package com.personal.nksnewfeed.retry;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("delayedRetryErrorHandler")
@RequiredArgsConstructor
public class DelayedRetryErrorHandler implements RabbitListenerErrorHandler {

    private final RabbitTemplate rabbitTemplate;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_DELAY_MS = 5000; // 5 seconds
    private static final int BACKOFF_MULTIPLIER = 2;

    @Override
    public Object handleError(final Message amqpMessage, final Channel channel,
                               final org.springframework.messaging.Message<?> message,
                               final ListenerExecutionFailedException exception) {
        log.error("Error processing message", exception.getCause());

        try {
            final long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
            final Integer retryCount = getRetryCount(amqpMessage);

            log.info("Current retry count: {}, MAX_RETRY_ATTEMPTS: {}", retryCount, MAX_RETRY_ATTEMPTS);

            if (retryCount < MAX_RETRY_ATTEMPTS) {
                handleDelayedRetry(amqpMessage, message.getPayload(), retryCount, channel, deliveryTag);
            } else {
                log.error("Max retry attempts reached (retryCount={}/{}). Sending to DLQ", retryCount, MAX_RETRY_ATTEMPTS);
                channel.basicNack(deliveryTag, false, false);
            }
        } catch (final IOException e) {
            log.error("Failed to handle error", e);
        }

        return null;
    }

    private void handleDelayedRetry(final Message amqpMessage, final Object payload, final Integer retryCount,
                                     final Channel channel, final long deliveryTag) throws IOException {
        final int nextRetryCount = retryCount + 1;
        final long delay = INITIAL_DELAY_MS * (long) Math.pow(BACKOFF_MULTIPLIER, retryCount);

        final String exchange = amqpMessage.getMessageProperties().getReceivedExchange();
        final String routingKey = amqpMessage.getMessageProperties().getReceivedRoutingKey();

        log.info("Scheduling retry {}/{} with delay={}ms for exchange={}, routingKey={}",
                nextRetryCount, MAX_RETRY_ATTEMPTS, delay, exchange, routingKey);

        try {
            // 1. 새로운 메시지를 delayed exchange에 발행 (x-delay 헤더로 지연 시간 설정)
            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    payload,
                    msg -> {
                        msg.getMessageProperties().setHeader("x-delay", Math.toIntExact(delay));
                        msg.getMessageProperties().setHeader("x-retry-count", nextRetryCount);
                        return msg;
                    }
            );

            // 2. 새 메시지 발행이 성공했으므로 원본 메시지를 큐에서 제거 (ACK)
            // 주의: rabbitTemplate.convertAndSend()가 내부적으로 채널 상태를 변경할 수 있어서
            //      basicAck 시 "unknown delivery tag" 에러가 발생할 수 있지만,
            //      메시지는 이미 재발행되었으므로 해당 에러는 무시해도 안전함
            try {
                channel.basicAck(deliveryTag, false);
            } catch (final Exception ackException) {
                log.warn("Failed to ACK message after successful retry publish. This is safe to ignore.", ackException);
            }

        } catch (final Exception e) {
            // 3. 새 메시지 발행에 실패한 경우, 원본 메시지를 다시 큐에 넣음 (NACK with requeue)
            // requeue=true로 설정하여 메시지 손실을 방지
            log.error("Failed to publish retry message. Requeuing original message.", e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (final Exception nackException) {
                log.error("Failed to NACK message. Message may be lost.", nackException);
            }
        }
    }

    private Integer getRetryCount(final Message message) {
        final Object retryCountHeader = message.getMessageProperties().getHeader("x-retry-count");
        return retryCountHeader != null ? (Integer) retryCountHeader : 0;
    }
}