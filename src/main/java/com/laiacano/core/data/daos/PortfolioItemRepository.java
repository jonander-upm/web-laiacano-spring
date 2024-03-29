package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.PortfolioItem;
import com.laiacano.core.data.entities.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface PortfolioItemRepository extends ReactiveCrudRepository<PortfolioItem, String> {
    @Query("SELECT * FROM portfolio_items " +
            "WHERE (name LIKE :name OR :name IS NULL) " +
            "AND (description LIKE :description OR :description IS NULL) " +
            "AND (uploaded_date = :uploadedDate OR :uploadedDate IS NULL)")
    Flux<PortfolioItem> findByNameAndDercriptionAndUploadedDateNullSafe(String name, String description, LocalDate uploadedDate);
}
