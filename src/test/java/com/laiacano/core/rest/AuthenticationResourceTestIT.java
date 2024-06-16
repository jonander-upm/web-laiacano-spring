package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Role;
import com.laiacano.core.rest.dtos.*;
import com.laiacano.core.services.MailingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
class AuthenticationResourceTestIT {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private MailingService mailingService;

    @Test
    void testCreate() {
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser" + Math.random())
                .password("Test123.")
                .email("test@test.test")
                .build();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.REGISTER)
                .body(Mono.just(registerDto), RegisterDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(Assertions::assertNotNull)
                .value(createdUser -> {
                    assertEquals(registerDto.getUsername(), createdUser.getUsername());
                    assertEquals("test@test.test", createdUser.getEmail());
                    assertEquals(Role.CUSTOMER, createdUser.getRole());
                });
    }

    @Test
    void testCreateRepeated() {
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser" + Math.random())
                .password("Test123.")
                .email("test@test.test")
                .build();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.REGISTER)
                .body(Mono.just(registerDto), RegisterDto.class)
                .exchange()
                .expectStatus().isOk();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.REGISTER)
                .body(Mono.just(registerDto), RegisterDto.class)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testLogin() {
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser" + Math.random())
                .password("Test123.")
                .email("test@test.test")
                .build();
        LoginDto loginDto = LoginDto.builder()
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .build();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.REGISTER)
                .body(Mono.just(registerDto), RegisterDto.class)
                .exchange()
                .expectStatus().isOk();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.LOGIN)
                .body(Mono.just(loginDto), LoginDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testLoginUserNotFound() {
        LoginDto loginDto = LoginDto.builder()
                .username("nonexistent" + Math.random())
                .password("Test" + Math.random())
                .build();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.LOGIN)
                .body(Mono.just(loginDto), RegisterDto.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testForgotPassword() {
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser" + Math.random())
                .password("Test123.")
                .email("test@test.test")
                .build();
        webTestClient
                .post()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.REGISTER)
                .body(Mono.just(registerDto), RegisterDto.class)
                .exchange()
                .expectStatus().isOk();

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AuthenticationResource.AUTHENTICATION + AuthenticationResource.FORGOT_PASSWORD)
                        .queryParam("username", registerDto.getUsername())
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testForgotPasswordUserNotFound() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AuthenticationResource.AUTHENTICATION + AuthenticationResource.FORGOT_PASSWORD)
                        .queryParam("username", "testuser" + Math.random())
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testResetPasswordTokenNotFound() {
        ResetPasswordDto resetPasswordDto = ResetPasswordDto.builder()
                .username("testuser")
                .token("token")
                .password("123456789")
                .build();
        webTestClient
                .put()
                .uri(AuthenticationResource.AUTHENTICATION + AuthenticationResource.RESET_PASSWORD)
                .body(Mono.just(resetPasswordDto), ResetPasswordDto.class)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
