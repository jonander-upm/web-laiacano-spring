package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Address;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends ReactiveMongoRepository<Address, String> {
}
