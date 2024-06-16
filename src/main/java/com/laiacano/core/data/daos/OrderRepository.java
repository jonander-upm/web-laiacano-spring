package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Order;
import com.laiacano.core.data.entities.Status;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
    @Query("{$and:[" // allow NULL: all elements
            + "?#{ [0] == null || [0] == '' ? {_id : {$ne:null}} : { userId : [0] } },"
            + "?#{ [1] == null ? {_id : {$ne:null}} : { status : [1] } },"
            + "?#{ [2] == null ? {_id : {$ne:null}} : {  createdDate: {$gte:[2]} } },"
            + "?#{ [3] == null ? {_id : {$ne:null}} : {  createdDate: {$lte:[3]} } },"
            + "] }")
    Flux<Order> findByUserIdAndStatusAndDateBetweenNullSafe(String userId, Status status, LocalDate dateFrom, LocalDate dateTo);

    Mono<Order> findByIdAndUserId(String id, String userId);
}
