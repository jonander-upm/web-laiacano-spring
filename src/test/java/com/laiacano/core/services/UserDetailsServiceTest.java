package com.laiacano.core.services;

import com.laiacano.core.data.daos.UserRepository;
import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.Mockito.when;

@SpringBootTest
class UserDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void testFindByUsernameUserFound() {
        User user = new User("1", "username", "email@example.com", "password", Role.CUSTOMER, null);

        when(userRepository.findByUsername("username")).thenReturn(Mono.just(user));

        StepVerifier.create(userDetailsService.findByUsername("username"))
                .expectNextMatches(userDetails ->
                        userDetails.getUsername().equals("username") &&
                                userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_CUSTOMER")
                )
                .verifyComplete();
    }

    @Test
    void testFindByUsernameUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername("nonexistent"))
                .expectError(UsernameNotFoundException.class)
                .verify();
    }
}