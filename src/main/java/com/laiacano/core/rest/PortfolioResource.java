package com.laiacano.core.rest;

import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
        return this.portfolioService.findByNameAndDercriptionAndUploadedDateNullSafe(name, description, uploadedDate);
    }
}
