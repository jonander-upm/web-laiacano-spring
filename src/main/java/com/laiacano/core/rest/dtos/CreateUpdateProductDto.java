package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Format;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateProductDto {
    private String id;
    private String portfolioItemId;
    private BigDecimal price;
    private Integer stock;
    private Format format;
    private Boolean disabled = false;

}
