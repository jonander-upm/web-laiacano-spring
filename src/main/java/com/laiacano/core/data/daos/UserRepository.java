package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByUsername(String username);
    Mono<User> findByUsernameAndResetPasswordToken(String username, String resetPasswordToken);
}
