package com.laiacano.core.rest.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDto {
    @NonNull
    private String name;
    private String description;
    @NonNull
    private String imageSrc;
}
