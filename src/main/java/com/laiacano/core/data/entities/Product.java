package com.laiacano.core.data.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import com.laiacano.core.rest.dtos.ProductDto;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    @NonNull
    private String portfolioItemId;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Integer stock;
    @NonNull
    private Format format;
    private Boolean disabled = false;

    public ProductDto toProductDto(PortfolioItemDto portfolioItemDto) {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(this, productDto);
        productDto.setPortfolioItem(portfolioItemDto);
        return productDto;
    }
}
