package com.laiacano.core.services;

import com.laiacano.core.data.daos.PortfolioItemRepository;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.rest.dtos.DisablePortfolioItemDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class PortfolioServiceTest {

    @MockBean
    private PortfolioItemRepository portfolioItemRepository;

    @Autowired
    private PortfolioService portfolioService;

    @Test
    void testGetPortfolioItemList() {
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        when(portfolioItemRepository.findByNameAndDescriptionAndUploadedDateNullSafe(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(Flux.just(portfolioItem));

        Flux<PortfolioItemDto> result = portfolioService.getPortfolioItemList("Name", "Description", LocalDate.now().toString());

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getId().equals("1") && dto.getName().equals("Name") && dto.getDescription().equals("Description"))
                .verifyComplete();
    }

    @Test
    void testGetPortfolioItem() {
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        when(portfolioItemRepository.findByIdAndDisabledFalse("1")).thenReturn(Mono.just(portfolioItem));

        Mono<PortfolioItemDto> result = portfolioService.getPortfolioItem("1");

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getId().equals("1") && dto.getName().equals("Name") && dto.getDescription().equals("Description"))
                .verifyComplete();
    }

    @Test
    void testCreate() {
        PortfolioItemDto portfolioItemDto = new PortfolioItemDto("1", "Name", "Description", "/test.jpg", false);
        PortfolioItem portfolioItem = new PortfolioItem(portfolioItemDto);
        when(portfolioItemRepository.save(Mockito.any(PortfolioItem.class))).thenReturn(Mono.just(portfolioItem));

        Mono<Void> result = portfolioService.create(portfolioItemDto);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        PortfolioItemDto portfolioItemDto = new PortfolioItemDto("1", "Name", "Description", "/test.jpg", false);
        PortfolioItem existingItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);
        PortfolioItem updatedItem = new PortfolioItem(portfolioItemDto);

        when(portfolioItemRepository.findByIdAndDisabledFalse("1")).thenReturn(Mono.just(existingItem));
        when(portfolioItemRepository.save(Mockito.any(PortfolioItem.class))).thenReturn(Mono.just(updatedItem));

        Mono<Void> result = portfolioService.update("1", portfolioItemDto);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testPatchDisabled() {
        DisablePortfolioItemDto disableDto = new DisablePortfolioItemDto("1", true);
        PortfolioItem portfolioItem = new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false);

        when(portfolioItemRepository.findById("1")).thenReturn(Mono.just(portfolioItem));
        when(portfolioItemRepository.save(Mockito.any(PortfolioItem.class))).thenReturn(Mono.just(portfolioItem));

        Flux<Void> result = portfolioService.patchDisabled(Collections.singletonList(disableDto));

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testGetImage() throws IOException {
        String fileName = "test.jpg";

        when(portfolioItemRepository.findByIdAndDisabledFalse("1")).thenReturn(Mono.just(new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false)));
        when(portfolioItemRepository.save(Mockito.any(PortfolioItem.class))).thenReturn(Mono.just(new PortfolioItem("1", "Name", "Description", "/test.jpg", LocalDate.now(), false)));

        Mono<Resource> result = portfolioService.getImage(fileName);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testUploadImage() {
        FilePart filePart = mock(FilePart.class);
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("dummy data".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("file", "test.jpg");
        headers.setContentType(MediaType.IMAGE_JPEG);

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(filePart.headers()).thenReturn(headers);
        when(filePart.filename()).thenReturn("test.jpg");
        when(filePart.transferTo((File) any())).thenReturn(Mono.empty());

        Mono<String> result = portfolioService.uploadImage(filePart);

        StepVerifier.create(result)
                .expectNextMatches(fileName -> {
                    String[] parts = fileName.split(PortfolioService.FILENAME_SEPARATOR);
                    return parts.length == 2 && parts[1].equals("test.jpg");
                })
                .verifyComplete();
    }
}