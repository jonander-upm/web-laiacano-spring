package com.laiacano.core.rest;

import com.laiacano.core.config.jwt.JwtTokenProvider;
import com.laiacano.core.rest.dtos.LoginDto;
import com.laiacano.core.rest.dtos.TokenDto;
import com.laiacano.core.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthenticationResource.AUTHENTICATION)
public class AuthenticationResource {
    protected static final String AUTHENTICATION = "/auth";
    private static final String LOGIN = "/login";

    private final AuthenticationService authenticationService;

    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(LOGIN)
    public Mono<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        return this.authenticationService.login(loginDto);
    }
}
