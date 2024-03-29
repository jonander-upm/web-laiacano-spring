package com.laiacano.core.services;

import com.laiacano.core.data.daos.PortfolioItemRepository;
import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.exceptions.BadRequestException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Service
public class PortfolioService {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final PortfolioItemRepository portfolioItemRepository;

    public PortfolioService(PortfolioItemRepository portfolioItemRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public Flux<PortfolioItem> findByNameAndDescriptionAndUploadedDateNullSafe(String name, String description, String uploadedDate) {
        LocalDate uploadedDateParsed = null;
        if(Objects.nonNull(uploadedDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.getDefault());
            uploadedDateParsed = LocalDate.parse(uploadedDate, formatter);
        }
        return this.portfolioItemRepository.findByNameAndDescriptionAndUploadedDateNullSafe(name, description, uploadedDateParsed);
    }

    public Mono<PortfolioItem> findById(String id) {
        return this.portfolioItemRepository.findById(id);
    }
}
