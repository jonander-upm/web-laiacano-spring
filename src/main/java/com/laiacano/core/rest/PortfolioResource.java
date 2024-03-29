package com.laiacano.core.rest;

import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(PortfolioResource.PORTFOLIO)
public class PortfolioResource {
    protected static final String PORTFOLIO = "/api/v1/portfolio";

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioResource(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Flux<PortfolioItem> viewPortfolio(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) String uploadedDate) {
        return this.portfolioService.findByNameAndDescriptionAndUploadedDateNullSafe(name, description, uploadedDate);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public Mono<PortfolioItem> viewPortfolioItem(@PathVariable String id) {
        return this.portfolioService.findById(id);
    }
}
