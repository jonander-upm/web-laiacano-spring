package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.PortfolioItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("dev")
class PortfolioItemRepositoryTest {
    String id;
    LocalDate uploadedDate;

    @Autowired
    private PortfolioItemRepository portfolioItemRepository;

    @BeforeEach
    void prepareTests() {
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();
        PortfolioItem createdPortfolioItem = this.portfolioItemRepository.save(portfolioItem).block();
        if(createdPortfolioItem != null) {
            this.id = createdPortfolioItem.getId();
            this.uploadedDate = createdPortfolioItem.getUploadedDate();
        }
    }

    @AfterEach
    void resetTests() {
        this.portfolioItemRepository.deleteById(this.id).block();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.portfolioItemRepository.findById(id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByNameAndDescriptionAndUploadedDateNullSafe() {
        StepVerifier
                .create(this.portfolioItemRepository
                        .findByNameAndDescriptionAndUploadedDateNullSafe(
                                "Test Name",
                                "Test Description",
                                this.uploadedDate
                        )
                )
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByIdAndDisabledFalse() {
        StepVerifier
                .create(this.portfolioItemRepository.findByIdAndDisabledFalse(this.id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
}
