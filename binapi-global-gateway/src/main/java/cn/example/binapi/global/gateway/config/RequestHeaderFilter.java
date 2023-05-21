package cn.example.binapi.global.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static cn.example.binapi.common.constant.CommonConstant.HEADER_NAME;
import static cn.example.binapi.common.constant.CommonConstant.HEADER_VALUE;

/**
 * @author Carl
 * @since 2023-05-21
 */
@Slf4j
@Component
public class RequestHeaderFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest httpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add(HEADER_NAME, HEADER_VALUE)).build();
        log.info(String.valueOf(httpRequest.getHeaders()));
        exchange.mutate().request(httpRequest); // 重置请求
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
