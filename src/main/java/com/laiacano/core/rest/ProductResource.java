package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
