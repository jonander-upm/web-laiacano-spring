package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Role;
import com.laiacano.core.data.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private Role role;

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this);
        this.role = user.getRole();
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        user.setRole(this.role);
        return user;
    }
}
