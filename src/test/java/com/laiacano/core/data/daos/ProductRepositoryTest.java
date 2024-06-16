package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@SpringBootTest
class ProductRepositoryTest {
    String id;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void prepareTests() {
        Product product = Product.builder()
                .portfolioItemId("test ID")
                .format(Format.PHYSICAL)
                .price(new BigDecimal(10))
                .stock(10)
                .disabled(false)
                .build();
        Product createdProduct = this.productRepository.save(product).block();
        if(createdProduct != null) {
            this.id = createdProduct.getId();
        }
    }

    @AfterEach
    void resetTests() {
        this.productRepository.deleteById(this.id).block();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.productRepository.findById(id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByPortfolioItemIdAndFormatNullSafe() {
        StepVerifier
                .create(this.productRepository
                        .findByPortfolioItemIdAndFormatNullSafe("test ID", Format.PHYSICAL)
                )
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByIdAndDisabledFalse() {
        StepVerifier
                .create(this.productRepository.findByIdAndDisabledFalse(this.id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
}
