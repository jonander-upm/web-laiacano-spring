package com.laiacano.core.data.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {

    @Test
    void testGenerateResetPasswordToken() {
        User user = User.builder()
                .username("tester")
                .password("123456789")
                .email("test@test.test")
                .role(Role.CUSTOMER)
                .resetPasswordToken("testToken")
                .build();
        String resetPasswordToken = user.generateResetPasswordToken();

        Assertions.assertNotNull(user.getResetPasswordToken());
        Assertions.assertEquals(resetPasswordToken, user.getResetPasswordToken());
    }
}
