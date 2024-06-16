package com.laiacano.core.rest;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import com.laiacano.core.rest.dtos.DisableProductDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import com.laiacano.core.rest.dtos.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
class ProductResourceTestIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;


    @Test
    void viewProductsTest() {
        webTestClient.get()
                .uri(ProductResource.PRODUCTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void viewProductTest() {
        webTestClient.get()
                .uri(ProductResource.PRODUCTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .value(productDtos -> {
                    webTestClient.get()
                            .uri(ProductResource.PRODUCTS + ProductResource.PRODUCT_ID, productDtos.get(0).getId())
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(ProductDto.class)
                            .value(Assertions::assertNotNull);
                });
    }

    @Test
    void createProductTest() {
        restClientTestService.loginManager(webTestClient)
                .get()
                .uri(PortfolioResource.PORTFOLIO)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortfolioItemDto.class)
                .value(portfolioItems -> {
                    Product product = Product.builder()
                            .price(new BigDecimal(10))
                            .stock(10)
                            .format(Format.PHYSICAL)
                            .portfolioItemId(portfolioItems.get(0).getId())
                            .disabled(false)
                            .build();
                    restClientTestService.loginManager(webTestClient)
                            .post()
                            .uri(ProductResource.PRODUCTS)
                            .bodyValue(product)
                            .exchange()
                            .expectStatus().isOk();
                });
    }

    @Test
    void updateProductTest() {
        restClientTestService.loginManager(webTestClient)
                .get()
                .uri(ProductResource.PRODUCTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .value(productDtos -> {
                    ProductDto productDto = productDtos.get(0);
                    Product product = Product.builder()
                            .price(productDto.getPrice())
                            .stock(productDto.getStock())
                            .format(Format.DIGITAL)
                            .portfolioItemId(productDto.getPortfolioItem().getId())
                            .disabled(false)
                            .build();
                    restClientTestService.loginManager(webTestClient)
                            .put()
                            .uri(ProductResource.PRODUCTS + ProductResource.PRODUCT_ID, productDto.getId())
                            .body(Mono.just(product), Product.class)
                            .exchange()
                            .expectStatus().isOk();
                });
    }

    @Test
    void setProductDisabledTest() {
        restClientTestService.loginManager(webTestClient)
                .get()
                .uri(ProductResource.PRODUCTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .value(productDtos -> {
                    DisableProductDto disableProductDto = DisableProductDto.builder()
                            .id(productDtos.get(0).getId())
                            .disabled(false)
                            .build();

                    restClientTestService.loginManager(webTestClient)
                            .patch()
                            .uri(ProductResource.PRODUCTS)
                            .body(Flux.just(disableProductDto), DisableProductDto.class)
                            .exchange()
                            .expectStatus()
                            .isOk();
                });
    }
}
