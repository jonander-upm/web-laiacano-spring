package com.laiacano.core.services;

import com.laiacano.core.data.daos.AddressRepository;
import com.laiacano.core.data.daos.OrderRepository;
import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.daos.UserRepository;
import com.laiacano.core.data.entities.*;
import com.laiacano.core.data.exceptions.BadRequestException;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.OrderDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Flux<OrderDto> getOrderList(String userId, Status status, LocalDate dateFrom, LocalDate dateTo) {
        return this.getCurrentUser().map(currentUser -> {
            if(currentUser.getRole() != Role.MANAGER) {
                return currentUser.getId();
            } else if(Objects.isNull(userId)) {
                return "";
            }
            return userId;
        })
        .flux()
        .flatMap(allowedUserId -> this.orderRepository.findByUserIdAndStatusAndDateBetweenNullSafe(allowedUserId, status, dateFrom, dateTo)
            .flatMap(this::mapOrderDto))
        ;
    }

    public Mono<OrderDto> getOrder(String id) {
        return this.findOrderOrError(id).flatMap(this::mapOrderDto);
    }

    private Mono<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User coreUser =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        return this.findUserOrError(coreUser.getUsername());
    }

    private Mono<Order> findOrderOrError(String id) {
        return this.getCurrentUser().flatMap(currentUser -> {
            if(currentUser.getRole() != Role.MANAGER) {
                return this.orderRepository.findByIdAndUserId(id, currentUser.getId());
            }
            return this.orderRepository.findById(id);
        })
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

    public Mono<Void> create(OrderDto orderDto) {
        return Mono.zip(
                this.findAddressOrCreate(orderDto.getShippingAddress()),
                this.findAddressOrCreate(orderDto.getBillingAddress()),
                this.getTotalPrice(orderDto),
                this.getCurrentUser()
        ).map(orderDataTuple -> orderDto.toOrder(
            orderDataTuple.getT1(),
            orderDataTuple.getT2(),
            orderDataTuple.getT3(),
            orderDataTuple.getT4().getId()
        ))
        .flatMap(orderRepository::save)
        .flatMap(savedOrder -> Mono.empty());
    }

    private Mono<String> findAddressOrCreate(Address address) {
        if(address.getId() != null) {
            return this.findAddressOrError(address.getId())
                    .map(Address::getId);
        }
        return this.addressRepository.save(address).map(Address::getId);
    }

    private Mono<BigDecimal> getTotalPrice(OrderDto orderDto) {
        return Flux.fromIterable(orderDto.getOrderItems())
                .flatMap(this::updateProductStockAndGetPrice)
                .reduce(BigDecimal::add);
    }

    private Mono<BigDecimal> updateProductStockAndGetPrice(OrderItem orderItem) {
        return this.findProductOrError(orderItem.getProductId()).flatMap(
            product -> {
                if(product.getStock() < orderItem.getAmount()) {
                    return Mono.error(new BadRequestException(
                            "Not enough stock for product with ID " + product.getId())
                    );
                }
                product.setStock(product.getStock() - orderItem.getAmount());
                return Mono.just(product);
            }
        )
        .flatMap(productRepository::save)
        .map(product -> product.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getAmount()))
        );
    }

    private Mono<Product> findProductOrError(String id) {
        return this.productRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + id + " not found")));
    }

    private Mono<User> findUserOrError(String username) {
        return this.userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException("User with username " + username + " not found")));
    }
}
