package com.laiacano.core.data.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "portfolio_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItem {
    @Id
    private String id;
    @NonNull
    private String name;
    private String description;
    @NonNull
    private String imageSrc;
    @NonNull
    private LocalDate uploadedDate;
    @NonNull
    private Boolean disabled;
}
