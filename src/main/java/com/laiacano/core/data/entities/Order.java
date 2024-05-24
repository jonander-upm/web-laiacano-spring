package com.laiacano.core.data.entities;

import com.laiacano.core.rest.dtos.OrderDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import com.laiacano.core.rest.dtos.ProductDto;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @NonNull
    private LocalDate createdDate;

    public OrderDto toOrderDto(Address shippingAddress, Address billingAddress) {
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(this, orderDto);
        orderDto.setShippingAddress(shippingAddress);
        orderDto.setBillingAddress(billingAddress);
        return orderDto;
    }
}
