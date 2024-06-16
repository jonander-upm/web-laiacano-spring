package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.OrderItem;
import com.laiacano.core.data.entities.Status;
import com.laiacano.core.rest.dtos.OrderDto;
import com.laiacano.core.rest.dtos.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
class OrderResourceTestIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testViewOrders() {
        restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(OrderResource.ORDERS)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testViewOrdersFiltered() {
        restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderResource.ORDERS)
                        .queryParam("status", Status.PENDING)
                        .build()
                )
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testViewOrder() {
        restClientTestService.loginCustomer(webTestClient)
                .get()
                .uri(OrderResource.ORDERS)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(OrderDto.class)
                .value(orders -> {
                    Assertions.assertFalse(orders.isEmpty());
                    restClientTestService.loginCustomer(webTestClient)
                            .get()
                            .uri(OrderResource.ORDERS + OrderResource.ORDER_ID, orders.get(0).getId())
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .expectBodyList(OrderDto.class)
                            .value(Assertions::assertNotNull);
                });
    }

    @Test
    void testCreateOrder() {
        webTestClient.get()
                .uri(ProductResource.PRODUCTS)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductDto.class)
                .value(products -> {
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
                    OrderItem orderItem = OrderItem.builder()
                            .productId(products.get(0).getId())
                            .amount(1)
                            .build();
                    OrderDto orderDto = Order.builder()
                            .orderItems(List.of(orderItem))
                            .price(new BigDecimal(10))
                            .userId("testID")
                            .shippingAddressId("testID")
                            .billingAddressId("testID")
                            .status(Status.PENDING)
                            .createdDate(LocalDate.now())
                            .build()
                            .toOrderDto(address, address);

                    restClientTestService.loginManager(webTestClient)
                            .post()
                            .uri(OrderResource.ORDERS)
                            .body(Mono.just(orderDto), OrderDto.class)
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .expectBody(OrderDto.class)
                            .value(createdOrder -> {
                                Assertions.assertNotNull(createdOrder);
                                Assertions.assertNotNull(createdOrder.getId());
                            });
                });
    }

    @Test
    void testCreateOrderProductNotFound() {
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
        OrderItem orderItem = OrderItem.builder()
                .productId("testID")
                .amount(1)
                .build();
        OrderDto orderDto = Order.builder()
                .orderItems(List.of(orderItem))
                .price(new BigDecimal(10))
                .userId("testID")
                .shippingAddressId("testID")
                .billingAddressId("testID")
                .status(Status.PENDING)
                .createdDate(LocalDate.now())
                .build()
                .toOrderDto(address, address);

        restClientTestService.loginManager(webTestClient)
                .post()
                .uri(OrderResource.ORDERS)
                .body(Mono.just(orderDto), OrderDto.class)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
