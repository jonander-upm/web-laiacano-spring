package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.data.entities.Status;
import com.laiacano.core.rest.dtos.DisableProductDto;
import com.laiacano.core.rest.dtos.OrderDto;
import com.laiacano.core.rest.dtos.ProductDto;
import com.laiacano.core.services.OrderService;
import com.laiacano.core.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(OrderResource.ORDERS)
public class OrderResource {
    protected static final String ORDERS = "/api/v1/orders";

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Flux<OrderDto> viewOrders(@RequestParam(required = false) String userId, @RequestParam(required = false) Status status, @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo) {
        return this.orderService.getOrderList(userId, status, dateFrom, dateTo);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Mono<OrderDto> viewOrder(@PathVariable String id) {
        return this.orderService.getOrder(id);
    }
}
