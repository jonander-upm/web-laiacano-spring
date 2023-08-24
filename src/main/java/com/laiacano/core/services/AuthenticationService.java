package com.laiacano.core.services;

import com.laiacano.core.config.jwt.JwtTokenProvider;
import com.laiacano.core.data.daos.UserRepository;
import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.entities.User;
import com.laiacano.core.data.exceptions.ConflictException;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.*;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder encoder;
    private final MailingService mailingService;

    public AuthenticationService(UserRepository userRepository, ReactiveAuthenticationManager authenticationManager,
                                 JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder encoder, MailingService mailingService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.encoder = encoder;
        this.mailingService = mailingService;
    }

    public Mono<TokenDto> login(LoginDto loginDto) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword()
                        ))
                .flatMap(auth -> {
                    org.springframework.security.core.userdetails.User user =
                            (org.springframework.security.core.userdetails.User) auth.getPrincipal();
                    return this.userRepository.findByUsername(user.getUsername());
                })
                .map(user -> new TokenDto(jwtTokenProvider.createToken(user.getUsername(), user.getEmail(), user.getRole())));
    }

    public Mono<UserDto> register(RegisterDto registerDto) {
        return this.assertUserNotExist(registerDto.getUsername())
                .then(Mono.justOrEmpty(registerDto))
                .map(dto -> {
                    User user = dto.toUser();
                    user.setPassword(this.encoder.encode(dto.getPassword()));
                    user.setRole(Role.CUSTOMER);
                    return user;
                })
                .flatMap(this.userRepository::save)
                .map(UserDto::new);
    }

    public Mono<Void> assertUserNotExist(String username) {
        return this.userRepository.findByUsername(username)
                .flatMap(user -> Mono.error(
                        new ConflictException("Username already exists: " + username)
                ));
    }

    public Mono<Void> requestPasswordChange(String username) {
        return this.userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User " + username + " not found")))
                .map(user -> {
                    user.generateResetPasswordToken();
                    try {
                        this.mailingService.sendResetPasswordMessage(user);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                    return user;
                })
                .flatMap(this.userRepository::save)
                .then();
    }

    public Mono<Void> resetPassword(ResetPasswordDto resetPasswordDto) {
        return this.userRepository.findByUsernameAndResetPasswordToken(
                    resetPasswordDto.getUsername(), resetPasswordDto.getToken())
                .switchIfEmpty(Mono.error(
                        new NotFoundException("User " + resetPasswordDto.getUsername() + " not found")
                ))
                .map(user -> {
                    user.setPassword(this.encoder.encode(resetPasswordDto.getPassword()));
                    user.setResetPasswordToken(null);
                    return user;
                })
                .flatMap(this.userRepository::save)
                .then();
    }
}
