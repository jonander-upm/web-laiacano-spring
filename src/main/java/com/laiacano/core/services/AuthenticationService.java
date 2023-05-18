package com.laiacano.core.services;

import com.laiacano.core.config.jwt.JwtTokenProvider;
import com.laiacano.core.rest.dtos.LoginDto;
import com.laiacano.core.rest.dtos.TokenDto;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthenticationService {
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(ReactiveAuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Mono<TokenDto> login(LoginDto loginDto) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword()
                        ))
                .map(auth -> {
                    User user = (User) auth.getPrincipal();
                    return new TokenDto(jwtTokenProvider.createToken(user));
                });
    }
}
