package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ProductResource.PRODUCTS)
public class ProductResource {
    protected static final String PRODUCTS = "/api/v1/products";

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Flux<Product> viewProducts(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) Format format) {
        return this.productService.getProductList(name, description, format);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Mono<Product> viewProduct(@PathVariable String id) {
        return this.productService.getProduct(id);
    }
}
