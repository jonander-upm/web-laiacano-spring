package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NonNull
    @Length(min = 8, max = 32)
    private String username;
    @NonNull
    @Email
    private String email;
    @Length(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$¡!%*¿?&=()#|<>\\-_])[A-Za-z\\d@.$¡!%*¿?&=()#|<>\\-_]{8,32}$")
    private String password;

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
