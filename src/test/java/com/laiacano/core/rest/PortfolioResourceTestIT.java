package com.laiacano.core.rest;

import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.rest.dtos.DisablePortfolioItemDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
class PortfolioResourceTestIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testViewPortfolio() {
        webTestClient
                .get()
                .uri(PortfolioResource.PORTFOLIO)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(PortfolioItemDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testViewPortfolioItemAndModifyAndSetDisabled() {
        webTestClient
                .get()
                .uri(PortfolioResource.PORTFOLIO)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(PortfolioItemDto.class)
                .value(portfolioItems -> {
                    Assertions.assertFalse(portfolioItems.isEmpty());

                    webTestClient
                            .get()
                            .uri(PortfolioResource.PORTFOLIO + PortfolioResource.PORTFOLIO_ITEM_ID, portfolioItems.get(0).getId())
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .expectBody(PortfolioItemDto.class)
                            .value(portfolioItem -> {
                                Assertions.assertNotNull(portfolioItem);
                                portfolioItem.setName("TestUpdate");
                                restClientTestService.loginManager(webTestClient)
                                        .put()
                                        .uri(PortfolioResource.PORTFOLIO + PortfolioResource.PORTFOLIO_ITEM_ID, portfolioItem.getId())
                                        .body(Mono.just(portfolioItem), PortfolioItemDto.class)
                                        .exchange()
                                        .expectStatus()
                                        .isOk();

                                DisablePortfolioItemDto disablePortfolioItemDto = DisablePortfolioItemDto.builder()
                                        .id(portfolioItem.getId())
                                        .disabled(true)
                                        .build();
                                restClientTestService.loginManager(webTestClient)
                                        .patch()
                                        .uri(PortfolioResource.PORTFOLIO)
                                        .body(Flux.just(disablePortfolioItemDto), DisablePortfolioItemDto.class)
                                        .exchange()
                                        .expectStatus()
                                        .isOk();
                            });
                });
    }

    @Test
    void testCreatePortfolioItem() {
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();

        restClientTestService.loginManager(webTestClient)
                .post()
                .uri(PortfolioResource.PORTFOLIO)
                .body(Mono.just(portfolioItem), PortfolioItemDto.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testCreatePortfolioItemUnauthorized() {
        PortfolioItem portfolioItem = PortfolioItem.builder()
                .name("Test Name")
                .description("Test Description")
                .imageSrc("/test.png")
                .uploadedDate(LocalDate.now())
                .disabled(false)
                .build();

        restClientTestService.loginCustomer(webTestClient)
                .post()
                .uri(PortfolioResource.PORTFOLIO)
                .body(Mono.just(portfolioItem), PortfolioItemDto.class)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void testUploadImageAndGetImage() {
        FilePart filePart = mock(FilePart.class);
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("dummy data".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("file", "testImage.jpg");
        headers.setContentType(MediaType.IMAGE_JPEG);

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(filePart.headers()).thenReturn(headers);
        when(filePart.filename()).thenReturn("testImage.jpg");

        MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
        multipartData.add("file", filePart);

        restClientTestService.loginManager(webTestClient)
                .post()
                .uri(PortfolioResource.PORTFOLIO + PortfolioResource.PORTFOLIO_IMAGES)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(imageSrc -> {
                    webTestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(PortfolioResource.PORTFOLIO + PortfolioResource.PORTFOLIO_IMAGES)
                                .queryParam("fileName", imageSrc).build()
                        )
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Resource.class);
                });
    }
}
