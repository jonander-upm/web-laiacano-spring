package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Status;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Address shippingAddress;
    private Address billingAddress;
    private List<String> productIds;
    private BigDecimal price;
    private Status status;
    private LocalDate createdDate;
}
