package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.PortfolioItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface PortfolioItemRepository extends ReactiveMongoRepository<PortfolioItem, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { name : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { description : {$regex:[1], $options: 'i'} } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { uploadedDate : [2] } },"
            + "{ disabled: false }"
            + "] }")
    Flux<PortfolioItem> findByNameAndDescriptionAndUploadedDateNullSafe(String name, String description, LocalDate uploadedDate);

    Mono<PortfolioItem> findByIdAndDisabledFalse(String id);
}
