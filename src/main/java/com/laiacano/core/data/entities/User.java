package com.laiacano.core.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @ManyToOne(optional = false)
    @JoinColumn(name = "roleId", referencedColumnName = "id")
    private Role role;
}
