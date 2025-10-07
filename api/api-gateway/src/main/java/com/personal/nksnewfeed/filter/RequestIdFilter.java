package com.personal.nksnewfeed.filter;

import com.personal.nksnewfeed.constants.FilterOrder;
import com.personal.nksnewfeed.constants.HeaderConstants;
import com.personal.nksnewfeed.exception.MissingHeaderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final String requestId = exchange.getRequest().getHeaders().getFirst(HeaderConstants.REQUEST_ID);

        if (!StringUtils.hasText(requestId)) {
            return Mono.error(new MissingHeaderException(HeaderConstants.REQUEST_ID));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_ID_FILTER;
    }
}
