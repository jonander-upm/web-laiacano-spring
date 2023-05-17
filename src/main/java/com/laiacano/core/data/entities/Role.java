package com.laiacano.core.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;
    @NonNull
    private String name;
}
