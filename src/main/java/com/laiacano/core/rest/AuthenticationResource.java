package com.laiacano.core.rest;

import com.laiacano.core.rest.dtos.LoginDto;
import com.laiacano.core.rest.dtos.RegisterDto;
import com.laiacano.core.rest.dtos.UserDto;
import com.laiacano.core.rest.dtos.TokenDto;
import com.laiacano.core.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthenticationResource.AUTHENTICATION)
public class AuthenticationResource {
    protected static final String AUTHENTICATION = "/api/v1/auth";
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";


    private final AuthenticationService authenticationService;

    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(LOGIN)
    public Mono<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        return this.authenticationService.login(loginDto);
    }

    @PostMapping(REGISTER)
    public Mono<UserDto> register(@RequestBody @Valid RegisterDto registerDto) {
        return this.authenticationService.register(registerDto);
    }
}
