package com.laiacano.core.rest.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioItemDto {
    private String id;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private String imageSrc;
    private Boolean disabled;
}
