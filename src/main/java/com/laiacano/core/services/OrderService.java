package com.laiacano.core.services;

import com.laiacano.core.data.daos.AddressRepository;
import com.laiacano.core.data.daos.OrderRepository;
import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.Status;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.OrderDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, AddressRepository addressRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
    }

    public Flux<OrderDto> getOrderList(String userId, Status status, LocalDate dateFrom, LocalDate dateTo) {
        return this.orderRepository.findByUserIdAndStatusAndDateBetweenNullSafe(userId, status, dateFrom, dateTo)
                .flatMap(this::mapOrderDto);
    }

    public Mono<OrderDto> getOrder(String id) {
        return this.findOrderOrError(id).flatMap(this::mapOrderDto);
    }

    private Mono<Order> findOrderOrError(String id) {
        return this.orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Order with id " + id + " not found")));
    }

    private Mono<Address> findAddressOrError(String id) {
        return this.addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Address with id " + id + " not found")));
    }

    private Mono<OrderDto> mapOrderDto(Order order) {
        return Mono.zip(
            this.findAddressOrError(order.getShippingAddressId()),
            this.findAddressOrError(order.getBillingAddressId())
        ).map(addressTuple -> order.toOrderDto(addressTuple.getT1(), addressTuple.getT2()));
    }
}
