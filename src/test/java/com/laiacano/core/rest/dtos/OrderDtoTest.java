package com.laiacano.core.rest.dtos;

import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.OrderItem;
import com.laiacano.core.data.entities.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class OrderDtoTest {

     @Test
    void testToOrder() {
         Address address = Address.builder()
                 .id("testID")
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
         OrderItem orderItem = OrderItem.builder()
                 .productId("testID")
                 .amount(1)
                 .build();
         OrderDto orderDto = OrderDto.builder()
                 .orderItems(List.of(orderItem))
                 .shippingAddress(address)
                 .billingAddress(address)
                 .status(Status.PENDING)
                 .createdDate(LocalDate.now())
                 .price(new BigDecimal(10))
                 .userId("testID")
                 .build();

         Order order = orderDto.toOrder(address.getId(), address.getId(), new BigDecimal(10), "testID");

         Assertions.assertNotNull(order);
         Assertions.assertEquals(order.getOrderItems(), orderDto.getOrderItems());
         Assertions.assertEquals(order.getShippingAddressId(), orderDto.getShippingAddress().getId());
         Assertions.assertEquals(order.getBillingAddressId(), orderDto.getBillingAddress().getId());
         Assertions.assertEquals(order.getStatus(), orderDto.getStatus());
         Assertions.assertEquals(order.getCreatedDate(), orderDto.getCreatedDate());
         Assertions.assertEquals(order.getPrice(), orderDto.getPrice());
         Assertions.assertEquals(order.getUserId(), orderDto.getUserId());
     }
}
