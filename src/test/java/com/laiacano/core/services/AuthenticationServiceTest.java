package com.laiacano.core.services;

import com.laiacano.core.config.jwt.JwtTokenProvider;
import com.laiacano.core.data.daos.UserRepository;
import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.entities.User;
import com.laiacano.core.data.exceptions.ConflictException;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthenticationServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private ReactiveAuthenticationManager authenticationManager;
    @MockBean
    private BCryptPasswordEncoder encoder;
    @MockBean
    private MailingService mailingService;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    public void testLogin() {
        LoginDto loginDto = new LoginDto("user", "password");
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User("user", "password", new ArrayList<>());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(auth));
        when(userRepository.findByUsername("user")).thenReturn(Mono.just(new User("", "user", "email@example.com", "password", Role.CUSTOMER, "")));
        when(jwtTokenProvider.createToken(anyString(), anyString(), any(Role.class))).thenReturn("token");

        Mono<TokenDto> result = authenticationService.login(loginDto);

        StepVerifier.create(result)
                .expectNextMatches(tokenDto -> tokenDto.getToken().equals("token"))
                .verifyComplete();
    }

    @Test
    public void testRegister() {
        RegisterDto registerDto = new RegisterDto("user", "email@example.com", "password");
        when(userRepository.findByUsername("user")).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(new User("", "user", "email@example.com", "encodedPassword", Role.CUSTOMER, "")));
        when(encoder.encode("password")).thenReturn("encodedPassword");

        Mono<UserDto> result = authenticationService.register(registerDto);

        StepVerifier.create(result)
                .expectNextMatches(userDto -> userDto.getUsername().equals("user"))
                .verifyComplete();
    }

    @Test
    public void testAssertUserNotExistUserExists() {
        when(userRepository.findByUsername("existingUser")).thenReturn(Mono.just(new User()));

        Mono<Void> result = authenticationService.assertUserNotExist("existingUser");

        StepVerifier.create(result)
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    public void testRequestPasswordChangeUserNotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(Mono.empty());

        Mono<Void> result = authenticationService.requestPasswordChange("nonexistentUser");

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    public void testResetPassword() {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("user", "token", "newPassword");

        User user = new User("", "user", "email@example.com", "oldPassword", Role.CUSTOMER, "");
        user.setResetPasswordToken("token");

        when(userRepository.findByUsernameAndResetPasswordToken("user", "token")).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");

        Mono<Void> result = authenticationService.resetPassword(resetPasswordDto);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
