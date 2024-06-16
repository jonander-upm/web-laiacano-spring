package com.laiacano.core.rest;

import com.laiacano.core.rest.dtos.DisablePortfolioItemDto;
import com.laiacano.core.rest.dtos.PortfolioItemDto;
import com.laiacano.core.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(PortfolioResource.PORTFOLIO)
public class PortfolioResource {
    protected static final String PORTFOLIO = "/api/v1/portfolio";
    protected static final String PORTFOLIO_ITEM_ID = "/{id}";
    protected static final String PORTFOLIO_IMAGES = "/images";

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioResource(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping()
    public Flux<PortfolioItemDto> viewPortfolio(@RequestParam(required = false) String name, @RequestParam(required = false) String description, @RequestParam(required = false) String uploadedDate) {
        return this.portfolioService.getPortfolioItemList(name, description, uploadedDate);
    }

    @GetMapping(PORTFOLIO_ITEM_ID)
    public Mono<PortfolioItemDto> viewPortfolioItem(@PathVariable String id) {
        return this.portfolioService.getPortfolioItem(id);
    }

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<Void> createPortfolioItem(@RequestBody PortfolioItemDto portfolioItemDto) {
        return this.portfolioService.create(portfolioItemDto);
    }

    @PutMapping(PORTFOLIO_ITEM_ID)
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<Void> modifyPortfolioItem(@PathVariable String id, @RequestBody PortfolioItemDto portfolioItemDto) {
        return this.portfolioService.update(id, portfolioItemDto);
    }

    @PatchMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public Flux<Void> setDisabledPortfolioItems(@RequestBody List<DisablePortfolioItemDto> disablePortfolioItemDtos) {
        return this.portfolioService.patchDisabled(disablePortfolioItemDtos);
    }

    @GetMapping(PORTFOLIO_IMAGES)
    public Mono<Resource> getImage(@RequestParam String fileName) {
        return this.portfolioService.getImage(fileName);
    }

    @PostMapping( PORTFOLIO_IMAGES)
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<String> uploadImage(@RequestPart("file") FilePart file) {
        return this.portfolioService.uploadImage(file);
    }
}
