package com.laiacano.core.services;

import com.laiacano.core.data.daos.AddressRepository;
import com.laiacano.core.data.daos.OrderRepository;
import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.daos.UserRepository;
import com.laiacano.core.data.entities.*;
import com.laiacano.core.rest.dtos.OrderDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Test
    void testGetOrderList() {
        User userDetails = new User("user", "password", new ArrayList<>());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = new Order();
        order.setId("orderId");
        order.setUserId("user");
        order.setShippingAddressId("shippingAddressId");
        order.setBillingAddressId("billingAddressId");

        when(userRepository.findByUsername("user")).thenReturn(Mono.just(new com.laiacano.core.data.entities.User("id", "user", "password", "email", Role.CUSTOMER, "")));
        when(orderRepository.findByUserIdAndStatusAndDateBetweenNullSafe(anyString(), any(), any(), any())).thenReturn(Flux.just(order));
        when(addressRepository.findById(anyString())).thenReturn(Mono.just(new Address()));

        Flux<OrderDto> result = orderService.getOrderList(null, null, null, null);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetOrderListManager() {
        User userDetails = new User("user", "password", new ArrayList<>());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = new Order();
        order.setId("orderId");
        order.setUserId("user");
        order.setShippingAddressId("shippingAddressId");
        order.setBillingAddressId("billingAddressId");

        when(userRepository.findByUsername("user")).thenReturn(Mono.just(new com.laiacano.core.data.entities.User("id", "user", "password", "email", Role.MANAGER, "")));
        when(orderRepository.findByUserIdAndStatusAndDateBetweenNullSafe(anyString(), any(), any(), any())).thenReturn(Flux.just(order));
        when(addressRepository.findById(anyString())).thenReturn(Mono.just(new Address()));

        Flux<OrderDto> result = orderService.getOrderList(null, null, null, null);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetOrderListNullUserId() {
        User userDetails = new User("user", "password", new ArrayList<>());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = new Order();
        order.setId("orderId");
        order.setUserId("user");
        order.setShippingAddressId("shippingAddressId");
        order.setBillingAddressId("billingAddressId");

        when(userRepository.findByUsername("user")).thenReturn(Mono.just(new com.laiacano.core.data.entities.User(null, "user", "password", "email", Role.MANAGER, "")));
        when(orderRepository.findByUserIdAndStatusAndDateBetweenNullSafe(anyString(), any(), any(), any())).thenReturn(Flux.just(order));
        when(addressRepository.findById(anyString())).thenReturn(Mono.just(new Address()));

        Flux<OrderDto> result = orderService.getOrderList(null, null, null, null);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetOrder() {
        User userDetails = new User("user", "password", new ArrayList<>());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Order order = new Order();
        order.setId("orderId");
        order.setUserId("user");
        order.setShippingAddressId("shippingAddressId");
        order.setBillingAddressId("billingAddressId");

        when(userRepository.findByUsername("user")).thenReturn(Mono.just(new com.laiacano.core.data.entities.User("testID", "user", "password", "email", Role.CUSTOMER, "")));
        when(orderRepository.findByIdAndUserId(anyString(), anyString())).thenReturn(Mono.just(order));
        when(addressRepository.findById(anyString())).thenReturn(Mono.just(new Address()));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));

        Mono<OrderDto> result = orderService.getOrder("orderId");

        StepVerifier.create(result)
                .expectNextMatches(orderDto -> orderDto.getId().equals("orderId"))
                .verifyComplete();
    }

    @Test
    void testCreateOrder() {
        User userDetails = new User("user", "password", new ArrayList<>());
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Address address = new Address();
        address.setId("addressId");

        OrderItem orderItem = new OrderItem("productId", 2);
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderItems(Arrays.asList(orderItem));
        orderDto.setShippingAddress(address);
        orderDto.setBillingAddress(address);

        Product product = new Product();
        product.setId("productId");
        product.setPrice(BigDecimal.TEN);
        product.setStock(10);

        com.laiacano.core.data.entities.User user = new com.laiacano.core.data.entities.User();
        user.setId("userId");

        when(addressRepository.findById(anyString())).thenReturn(Mono.just(address));
        when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));
        when(productRepository.findById(anyString())).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(new Order("orderId", "TestID", "TestID", new ArrayList<>(), new BigDecimal(10), Status.PENDING, LocalDate.now(), "")));

        Mono<OrderDto> result = orderService.create(orderDto);

        StepVerifier.create(result)
                .expectNextMatches(orderDtoResult -> orderDtoResult.getId().equals("orderId"))
                .verifyComplete();
    }
}