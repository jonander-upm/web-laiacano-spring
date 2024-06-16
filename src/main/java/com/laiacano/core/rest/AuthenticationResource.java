package com.laiacano.core.rest;

import com.laiacano.core.rest.dtos.*;
import com.laiacano.core.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthenticationResource.AUTHENTICATION)
public class AuthenticationResource {
    protected static final String AUTHENTICATION = "/api/v1/auth";
    protected static final String LOGIN = "/login";
    protected static final String REGISTER = "/register";
    protected static final String FORGOT_PASSWORD = "/forgot-password";
    protected static final String RESET_PASSWORD = "/reset-password";


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

    @GetMapping(FORGOT_PASSWORD)
    public Mono<Void> requestPasswordChange(@RequestParam String username) {
        return this.authenticationService.requestPasswordChange(username);
    }

    @PutMapping(RESET_PASSWORD)
    public Mono<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        return this.authenticationService.resetPassword(resetPasswordDto);
    }
}
