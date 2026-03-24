package net.czming.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RtGlobalFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        Long startTimestamp = System.currentTimeMillis();
        log.info("请求【{}】开始：时间：{}", request.getURI(), startTimestamp);


        return chain.filter(exchange)
                .doFinally(result -> {
                    Long endTimestamp = System.currentTimeMillis();
                    log.info("请求【{}】结束：时间：{}， 耗时：{}ms", request.getURI(), endTimestamp, endTimestamp - startTimestamp);
                });
    }
}
