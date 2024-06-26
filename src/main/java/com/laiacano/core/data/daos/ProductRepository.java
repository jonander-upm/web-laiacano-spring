package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { portfolioItemId : [0] } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { format : [1] } },"
            + "{ disabled: false }"
            + "] }")
    Flux<Product> findByPortfolioItemIdAndFormatNullSafe(String portfolioItemId, Format format);

    Mono<Product> findByIdAndDisabledFalse(String id);
}
