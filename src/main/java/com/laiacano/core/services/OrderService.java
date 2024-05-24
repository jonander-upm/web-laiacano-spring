package com.laiacano.core.services;

import com.laiacano.core.data.daos.AddressRepository;
import com.laiacano.core.data.daos.OrderRepository;
import com.laiacano.core.data.entities.Address;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.OrderDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, AddressRepository addressRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
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
