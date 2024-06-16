package com.laiacano.core.data.entities;

import com.laiacano.core.rest.dtos.PortfolioItemDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

@SpringBootTest
public class PortfolioItemTest {

    @Test
    void testBuilder() {
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();
        PortfolioItemDto portfolioItemDto = portfolioItem.toPortfolioItemDto();

        PortfolioItem portfolioItemCopy = new PortfolioItem(portfolioItemDto);
        Assertions.assertEquals(portfolioItem.getName(), portfolioItemCopy.getName());
        Assertions.assertEquals(portfolioItem.getDescription(), portfolioItemCopy.getDescription());
        Assertions.assertEquals(portfolioItem.getImageSrc(), portfolioItemCopy.getImageSrc());
        Assertions.assertEquals(portfolioItem.getDisabled(), portfolioItemCopy.getDisabled());
    }
    
    @Test
    void testToPortfolioItemDto() {
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();
        PortfolioItemDto portfolioItemDto = portfolioItem.toPortfolioItemDto();

        Assertions.assertEquals(portfolioItem.getName(), portfolioItemDto.getName());
        Assertions.assertEquals(portfolioItem.getDescription(), portfolioItemDto.getDescription());
        Assertions.assertEquals(portfolioItem.getImageSrc(), portfolioItemDto.getImageSrc());
        Assertions.assertEquals(portfolioItem.getDisabled(), portfolioItemDto.getDisabled());
    }
}
