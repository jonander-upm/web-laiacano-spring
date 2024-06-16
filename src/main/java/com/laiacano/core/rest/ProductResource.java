package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.rest.dtos.CreateUpdateProductDto;
import com.laiacano.core.rest.dtos.DisableProductDto;
import com.laiacano.core.rest.dtos.ProductDto;
import com.laiacano.core.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(ProductResource.PRODUCTS)
public class ProductResource {
    protected static final String PRODUCTS = "/api/v1/products";
    protected static final String PRODUCT_ID = "/{id}";

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public Flux<ProductDto> viewProducts(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) Format format, @RequestParam(required = false) String portfolioItemId) {
        return this.productService.getProductList(name, description, format, portfolioItemId);
    }

    @GetMapping("{id}")
    public Mono<ProductDto> viewProduct(@PathVariable String id) {
        return this.productService.getProduct(id);
    }

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<Void> createProduct(@RequestBody CreateUpdateProductDto product) {
        return this.productService.create(product);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<Void> updateProduct(@PathVariable String id, @RequestBody CreateUpdateProductDto product) {
        return this.productService.update(id, product);
    }

    @PatchMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public Flux<Void> setProductDisabled(@RequestBody List<DisableProductDto> disableProductDtos) {
        return this.productService.patchDisabled(disableProductDtos);
    }
}
