package com.laiacano.core.data.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("portfolio_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItem {
    @Id
    private Integer id;
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
