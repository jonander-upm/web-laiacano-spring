package com.laiacano.core.services;

import com.laiacano.core.data.daos.PortfolioItemRepository;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.exceptions.BadRequestException;
import com.laiacano.core.data.exceptions.NotFoundException;
import com.laiacano.core.rest.dtos.DisablePortfolioItemDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class PortfolioService {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String FILENAME_SEPARATOR = "_";

    private final String baseFilePath;
    private final PortfolioItemRepository portfolioItemRepository;

    public PortfolioService(@Value("${files.path}") String baseFilePath, PortfolioItemRepository portfolioItemRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
        this.baseFilePath = baseFilePath;
    }

    public Flux<PortfolioItemDto> getPortfolioItemList(String name, String description, String uploadedDate) {
        LocalDate uploadedDateParsed = null;
        if(Objects.nonNull(uploadedDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.getDefault());
            uploadedDateParsed = LocalDate.parse(uploadedDate, formatter);
        }
        return this.portfolioItemRepository.findByNameAndDescriptionAndUploadedDateNullSafe(name, description, uploadedDateParsed)
                .map(PortfolioItem::toPortfolioItemDto);
    }

    public Mono<PortfolioItemDto> getPortfolioItem(String id) {
        return this.findPortfolioItemOrError(id)
                .map(PortfolioItem::toPortfolioItemDto);
    }

    public Mono<Void> create(PortfolioItemDto portfolioItemDto) {
        return this.portfolioItemRepository.save(new PortfolioItem(portfolioItemDto))
                .flatMap(portfolioItem -> Mono.empty());
    }

    public Mono<String> uploadImage(FilePart image) {
        String fileName = UUID.randomUUID() + FILENAME_SEPARATOR + image.filename();
        File file = new File(baseFilePath + fileName);
        return image.transferTo(file).then(Mono.just(file.getAbsolutePath()));
    }

    public Mono<Void> update(String id, PortfolioItemDto portfolioItemDto) {
        return this.findPortfolioItemOrError(id)
                .map(portfolioItem -> {
                    BeanUtils.copyProperties(portfolioItemDto, portfolioItem, "id");
                    return portfolioItem;
                })
                .flatMap(this.portfolioItemRepository::save)
                .flatMap(portfolioItem -> Mono.empty());
    }

    public Flux<Void> patchDisabled(List<DisablePortfolioItemDto> disablePortfolioItemDtos) {
        return Flux.fromIterable(disablePortfolioItemDtos)
                .flatMap(disablePortfolioItemDto -> {
                    String id = disablePortfolioItemDto.getId();
                    return this.portfolioItemRepository.findById(id)
                        .map(portfolioItem -> {
                            BeanUtils.copyProperties(disablePortfolioItemDto, portfolioItem, "id");
                            return portfolioItem;
                        });
                })
                .flatMap(this.portfolioItemRepository::save)
                .flatMap(portfolioItem -> Mono.empty());
    }

    private Mono<PortfolioItem> findPortfolioItemOrError(String id) {
        return this.portfolioItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Item with id " + id + "not found")));
    }
}
