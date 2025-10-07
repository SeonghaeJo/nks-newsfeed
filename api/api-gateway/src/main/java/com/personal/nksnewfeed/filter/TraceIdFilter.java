package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.FilterOrder;
import com.personal.nksnewfeed.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final String traceId = UUID.randomUUID().toString();

        final ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder.header(HeaderConstants.TRACE_ID, traceId))
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.TRACE_ID_FILTER;
    }
}
