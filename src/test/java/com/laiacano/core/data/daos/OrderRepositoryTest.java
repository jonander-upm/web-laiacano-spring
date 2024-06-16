package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.OrderItem;
import com.laiacano.core.data.entities.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class OrderRepositoryTest {
    String id;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void prepareTests() {
        OrderItem orderItem = OrderItem.builder()
                .productId("testID")
                .amount(1)
                .build();
        Order order = Order.builder()
                .orderItems(List.of(orderItem))
                .shippingAddressId("testID")
                .billingAddressId("testID")
                .status(Status.PENDING)
                .createdDate(LocalDate.now())
                .price(new BigDecimal(10))
                .userId("testID")
                .build();
        Order createdOrder = this.orderRepository.save(order).block();
        if(createdOrder != null) {
            this.id = createdOrder.getId();
        }
    }

    @AfterEach
    void resetTests() {
        this.orderRepository.deleteById(this.id).block();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.orderRepository.findById(id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserIdAndStatusAndDateBetweenNullSafe() {
        StepVerifier
                .create(this.orderRepository
                        .findByUserIdAndStatusAndDateBetweenNullSafe(
                                "testID",
                                Status.PENDING,
                                LocalDate.now().minusDays(1),
                                LocalDate.now().plusDays(1)
                        )
                )
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByIdAndUserId() {
        StepVerifier
                .create(this.orderRepository
                        .findByIdAndUserId(
                                this.id,
                                "testID"
                        )
                )
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
}
