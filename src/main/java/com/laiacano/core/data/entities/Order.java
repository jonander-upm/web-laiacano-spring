package com.laiacano.core.data.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    @NonNull
    private String shippingAddressId;
    @NonNull
    private String billingAddressId;
    @NonNull
    private List<String> productIds;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Status status;
}
