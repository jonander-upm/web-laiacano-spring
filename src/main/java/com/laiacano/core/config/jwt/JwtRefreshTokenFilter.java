package com.laiacano.core.config.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class JwtRefreshTokenFilter implements WebFilter {
    private static final String TOKEN_HEADER = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;

    public JwtRefreshTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(TOKEN_HEADER);
        if(authHeader == null) {
            return chain.filter(exchange);
        }
        String token = authHeader.replace(AUTHORIZATION_TYPE, "");
        return chain.filter(
            exchange.mutate().request(
                exchange.getRequest().mutate()
                    .header(TOKEN_HEADER, AUTHORIZATION_TYPE +
                            this.jwtTokenProvider.refreshToken(token))
                    .build()
            )
            .build()
        );
    }
}
