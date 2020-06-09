package com.changgou.system.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IpFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String hostName = request.getRemoteAddress().getAddress().getHostName();
        String hostString = request.getRemoteAddress().getHostString();
        String hostAddress = request.getRemoteAddress().getAddress().getHostAddress();
        System.out.println("====ip====:"+hostName);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
