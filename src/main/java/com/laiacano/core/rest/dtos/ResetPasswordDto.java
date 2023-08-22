package com.laiacano.core.rest.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    @NonNull
    private String username;
    @NonNull
    private String token;
    @NonNull
    private String password;
}
