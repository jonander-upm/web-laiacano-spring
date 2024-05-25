package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.OrderItem;
import com.laiacano.core.data.entities.Status;
import lombok.*;
import org.springframework.beans.BeanUtils;
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
    private List<OrderItem> orderItems;
    private BigDecimal price;
    private Status status;
    private LocalDate createdDate;
    private String userId;

    public Order toOrder(String shippingAddressId, String billingAddressId, BigDecimal price, String userId) {
        Order order = new Order();
        BeanUtils.copyProperties(this, order, "createdDate", "price", "status", "userId");
        order.setShippingAddressId(shippingAddressId);
        order.setBillingAddressId(billingAddressId);
        order.setUserId(userId);
        order.setPrice(price);
        order.setCreatedDate(LocalDate.now());
        order.setStatus(Status.PENDING);
        return order;
    }
}
