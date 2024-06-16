package com.laiacano.core.data.entities;

import com.laiacano.core.rest.dtos.OrderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class OrderTest {

    @Test
    void testToOrderDto() {
        List<OrderItem> orderItems = List.of(OrderItem.builder()
                .productId("testID")
                .amount(1)
                .build());
        Order order = Order.builder()
                .orderItems(orderItems)
                .shippingAddressId("testID")
                .billingAddressId("testID")
                .status(Status.PENDING)
                .createdDate(LocalDate.now())
                .price(new BigDecimal(10))
                .userId("testID")
                .build();
        Address address = Address.builder()
                .firstName("Test")
                .lastName("Testison")
                .phoneNumber("000000000")
                .country("Testland")
                .state("Testshire")
                .city("Test City")
                .postcode("00000")
                .addressLine("Test Street 123")
                .additionalAddressLine("2 C")
                .build();

        OrderDto orderDto = order.toOrderDto(address, address);
        Assertions.assertEquals(orderDto.getStatus(), order.getStatus());
        Assertions.assertEquals(orderDto.getPrice(), order.getPrice());
        Assertions.assertEquals(orderDto.getCreatedDate(), order.getCreatedDate());
        Assertions.assertEquals(orderDto.getUserId(), order.getUserId());
        Assertions.assertEquals(orderDto.getOrderItems(), orderItems);
        Assertions.assertEquals(orderDto.getShippingAddress(), address);
        Assertions.assertEquals(orderDto.getBillingAddress(), address);
    }
}
