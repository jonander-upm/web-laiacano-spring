package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Format;
import com.laiacano.core.data.entities.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null ? {_id : {$ne:null}} : { name : {$regex:[0], $options: 'i'} } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { description : {$regex:[1], $options: 'i'} } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : { format : [2] } },"
            + "{ disabled: false }"
            + "] }")
    Flux<Product> findByNameAndDescriptionAndFormatNullSafe(String name, String description, Format format);
}
