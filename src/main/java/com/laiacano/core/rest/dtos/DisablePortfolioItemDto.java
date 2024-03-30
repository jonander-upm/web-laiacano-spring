package com.laiacano.core.rest.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisablePortfolioItemDto {
    @NonNull
    private String id;
    @NonNull
    private Boolean disabled;
}
