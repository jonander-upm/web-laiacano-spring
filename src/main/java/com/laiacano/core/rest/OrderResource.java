package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Status;
import com.laiacano.core.rest.dtos.OrderDto;
import com.laiacano.core.services.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping(OrderResource.ORDERS)
public class OrderResource {
    protected static final String ORDERS = "/api/v1/orders";
    protected static final String ORDER_ID = "/{id}";

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Flux<OrderDto> viewOrders(@RequestParam(required = false) String userId, @RequestParam(required = false) Status status, @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo) {
        return this.orderService.getOrderList(userId, status, dateFrom, dateTo);
    }

    @GetMapping(ORDER_ID)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Mono<OrderDto> viewOrder(@PathVariable String id) {
        return this.orderService.getOrder(id);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('MANAGER')")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        return this.orderService.create(orderDto);
    }
}
