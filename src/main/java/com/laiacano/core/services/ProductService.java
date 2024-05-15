package com.laiacano.core.services;

import com.laiacano.core.data.daos.PortfolioItemRepository;
import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.DisablePortfolioItemDto;
import com.laiacano.core.rest.dtos.DisableProductDto;
import com.laiacano.core.rest.dtos.ProductDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public ProductService(ProductRepository productRepository, PortfolioItemRepository portfolioItemRepository) {
        this.productRepository = productRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }
    public Flux<ProductDto> getProductList(String name, String description, Format format) {
        return this.portfolioItemRepository.findByNameAndDescriptionAndUploadedDateNullSafe(name, description, null)
            .flatMap(portfolioItem ->
                        productRepository.findByPortfolioItemIdAndFormatNullSafe(portfolioItem.getId(), format)
            ).flatMap(this::mapProductDto);
    }

    public Mono<ProductDto> getProduct(String id) {
        return this.findProductOrError(id)
                .flatMap(this::mapProductDto);
    }

    public Mono<Void> create(Product product) {
        return this.findPortfolioItemOrError(product.getPortfolioItemId())
                .map(portfolioItem -> {
                    if(product.getDisabled() == null) {
                        product.setDisabled(false);
                    }
                    return product;
                })
                .flatMap(productRepository::save)
                .flatMap(savedProduct -> Mono.empty());
    }

    public Mono<Void> update(String id, Product product) {
        return this.findPortfolioItemOrError(product.getPortfolioItemId())
            .flatMap(portfolioItem -> this.findProductOrError(id)
                .map(productItem -> {

                    BeanUtils.copyProperties(product, productItem, "id");
                    return productItem;
                }))
            .flatMap(productRepository::save)
            .flatMap(savedProduct -> Mono.empty());
    }

    private Mono<Product> findProductOrError(String id) {
        return this.productRepository.findByIdAndDisabledFalse(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + id + " not found")));
    }

    public Flux<Void> patchDisabled(List<DisableProductDto> disableProductDtos) {
        return Flux.fromIterable(disableProductDtos)
                .flatMap(disableProductDto -> {
                    String id = disableProductDto.getId();
                    return this.productRepository.findById(id)
                            .map(product -> {
                                BeanUtils.copyProperties(disableProductDto, product, "id");
                                return product;
                            });
                })
                .flatMap(this.productRepository::save)
                .flatMap(product -> Mono.empty());
    }

    private Mono<PortfolioItem> findPortfolioItemOrError(String id) {
        return this.portfolioItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Portfolio item with id " + id + " not found")));
    }

    private Mono<ProductDto> mapProductDto(Product product) {
        return this.findPortfolioItemOrError(product.getPortfolioItemId())
                .map(portfolioItem -> product.toProductDto(portfolioItem.toPortfolioItemDto()));
    }
}
