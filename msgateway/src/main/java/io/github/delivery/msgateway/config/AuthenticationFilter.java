package io.github.delivery.msgateway.config;

import io.github.delivery.msgateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter {

    private final JwtService jwtService;

    private final RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if(routerValidator.isSecured.test(request)) {

            final String authHeader = this.getAuthHeader(request);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange);
            }

            final String token = authHeader.substring(7);

            if (!jwtService.isTokenValid(token)) {
                return this.onError(exchange);
            }

            ServerWebExchange modifiedExchange = populateRequestWithHeaders(exchange, token);
            return chain.filter(modifiedExchange);

        }
        return chain.filter(exchange);

    }

    private String getAuthHeader(ServerHttpRequest request) {
        var headers = request.getHeaders().getOrEmpty("Authorization");
        return headers.isEmpty() ? null : headers.get(0);
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private ServerWebExchange populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        var email = jwtService.extractUsername(token);
        var role = jwtService.extractRole(token);
        var id = jwtService.extractId(token);

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role)
                .header("X-User-Id", id)
                .build();

        return exchange.mutate().request(request).build();

    }
}
