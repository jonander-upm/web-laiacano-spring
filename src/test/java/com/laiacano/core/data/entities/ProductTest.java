package com.laiacano.core.data.entities;

import com.laiacano.core.rest.dtos.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
public class ProductTest {

    @Test
    void testToProductDto() {
        Product product = Product.builder()
                .portfolioItemId("test ID")
                .format(Format.PHYSICAL)
                .price(new BigDecimal(10))
                .stock(10)
                .disabled(false)
                .build();
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .id("test ID")
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();
        ProductDto productDto = product.toProductDto(portfolioItem.toPortfolioItemDto());

        Assertions.assertEquals(product.getPortfolioItemId(), productDto.getPortfolioItem().getId());
        Assertions.assertEquals(product.getFormat(), productDto.getFormat());
        Assertions.assertEquals(product.getPrice(), productDto.getPrice());
        Assertions.assertEquals(product.getStock(), productDto.getStock());
        Assertions.assertEquals(product.getDisabled(), productDto.getDisabled());
    }
}
