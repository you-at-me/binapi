package cn.example.binapi.interfaces.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static cn.example.binapi.common.constant.CommonConstant.*;

/**
 * @author Carl
 * @since 2023-05-21
 */
@Slf4j
@Component
@Order(-2)
public class InterfacesRequestHeaderFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest httpRequest = request.mutate().headers(httpHeaders -> httpHeaders.add(INTERFACE_HEADER_NAME, INTERFACE_HEADER_VALUE)).build();
        log.info(String.valueOf(httpRequest.getHeaders()));
        exchange.mutate().request(httpRequest); // reset request
        return chain.filter(exchange);
    }

}
