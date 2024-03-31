package com.laiacano.core.data.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
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
    @DBRef
    private PortfolioItem portfolioItem;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Integer stock;
    @NonNull
    private Format format;
    @NonNull
    private Boolean disabled;
}
