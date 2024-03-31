package com.laiacano.core.services;

import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Flux<Product> getProductList(String name, String description, Format format) {
        return this.productRepository.findByNameAndDescriptionAndFormatNullSafe(name, description, format);
    }
}
