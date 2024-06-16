package com.laiacano.core.services;

import com.laiacano.core.data.daos.PortfolioItemRepository;
import com.laiacano.core.data.daos.ProductRepository;
import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.rest.dtos.CreateUpdateProductDto;
import com.laiacano.core.rest.dtos.DisableProductDto;
import com.laiacano.core.rest.dtos.ProductDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private PortfolioItemRepository portfolioItemRepository;

    @Autowired
    private  ProductService productService;

    @Test
    void testGetProductList() {
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        Product product = new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);

        when(portfolioItemRepository.findByNameAndDescriptionAndUploadedDateNullSafe(anyString(), anyString(), any()))
                .thenReturn(Flux.just(portfolioItem));
        when(productRepository.findByPortfolioItemIdAndFormatNullSafe(anyString(), any()))
                .thenReturn(Flux.just(product));
        when(portfolioItemRepository.findById(anyString()))
                .thenReturn(Mono.just(portfolioItem));


        Flux<ProductDto> result = productService.getProductList("Name", "Description", Format.DIGITAL, null);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getId().equals("1") && dto.getPortfolioItem().getName().equals("Name") && dto.getPortfolioItem().getDescription().equals("Description"))
                .verifyComplete();
    }

    @Test
    void testGetProduct() {
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        Product product = new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);

        when(productRepository.findByIdAndDisabledFalse("1"))
                .thenReturn(Mono.just(product));
        when(portfolioItemRepository.findById(anyString()))
                .thenReturn(Mono.just(portfolioItem));

        Mono<ProductDto> result = productService.getProduct("1");

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getId().equals("1") && dto.getPortfolioItem().getName().equals("Name") && dto.getPortfolioItem().getDescription().equals("Description"))
                .verifyComplete();
    }

    @Test
    void testCreate() {
        CreateUpdateProductDto productDto = new CreateUpdateProductDto("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);

        when(portfolioItemRepository.findById("1")).thenReturn(Mono.just(portfolioItem));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(Mono.just(new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false)));

        Mono<Void> result = productService.create(productDto);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        CreateUpdateProductDto productDto = new CreateUpdateProductDto("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        Product existingProduct = new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);

        when(portfolioItemRepository.findById("1")).thenReturn(Mono.just(portfolioItem));
        when(productRepository.findByIdAndDisabledFalse("1")).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(Mono.just(new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false)));

        Mono<Void> result = productService.update("1", productDto);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testPatchDisabled() {
        DisableProductDto disableDto = new DisableProductDto("1", true);
        Product product = new Product("1", "1", new BigDecimal(10), 10, Format.DIGITAL, false);

        when(productRepository.findById("1")).thenReturn(Mono.just(product));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(Mono.just(product));

        Flux<Void> result = productService.patchDisabled(Collections.singletonList(disableDto));

        StepVerifier.create(result)
                .verifyComplete();
    }
}