package com.laiacano.core.services;

import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.data.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Flux<Product> getProductList(String name, String description, Format format) {
        return this.productRepository.findByNameAndDescriptionAndFormatNullSafe(name, description, format);
    }

    public Mono<Product> getProduct(String id) {
        return this.findProductOrError(id);
    }

    private Mono<Product> findProductOrError(String id) {
        return this.productRepository.findByIdAndDisabledFalse(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + id + " not found")));
    }
}
