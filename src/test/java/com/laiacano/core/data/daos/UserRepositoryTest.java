package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;


@SpringBootTest
@ActiveProfiles("dev")
class UserRepositoryTest {
    String id;
    String username;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void prepareTests() {
        User user = User.builder()
                .username("tester")
                .password("123456789")
                .email("test@test.test")
                .role(Role.CUSTOMER)
                .resetPasswordToken("testToken")
                .build();
        User createdUser = this.userRepository.save(user).block();
        if(createdUser != null) {
            this.id = createdUser.getId();
            this.username = createdUser.getUsername();
        }
    }

    @AfterEach
    void resetTests() {
        this.userRepository.deleteById(this.id).block();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.userRepository.findById(id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUsername() {
        StepVerifier
                .create(this.userRepository.findByUsername(this.username))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUsernameAndResetPasswordToken() {
        StepVerifier
                .create(this.userRepository
                        .findByUsernameAndResetPasswordToken(this.username, "testToken")
                )
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
}
