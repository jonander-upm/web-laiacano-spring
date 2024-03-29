package com.laiacano.core.data.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
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
