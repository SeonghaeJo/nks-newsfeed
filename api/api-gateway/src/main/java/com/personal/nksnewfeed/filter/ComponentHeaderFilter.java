package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.FilterOrder;
import com.personal.nksnewfeed.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ComponentHeaderFilter implements GlobalFilter, Ordered {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder.header(HeaderConstants.COMPONENT, applicationName))
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.COMPONENT_HEADER_FILTER;
    }
}