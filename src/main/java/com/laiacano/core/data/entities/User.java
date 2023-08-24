package com.laiacano.core.data.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Integer id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    private Role role;
    private String resetPasswordToken;

    public String generateResetPasswordToken() {
        this.resetPasswordToken = UUID.randomUUID().toString();
        return this.resetPasswordToken;
    }
}
