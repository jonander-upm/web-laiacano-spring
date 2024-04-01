package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Format;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    @NonNull
    private PortfolioItemDto portfolioItem;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Integer stock;
    @NonNull
    private Format format;
    private Boolean disabled;
}
