package com.laiacano.core.data.daos;

import com.laiacano.core.data.entities.Address;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("local")
class AddressRepositoryTest {
    String id;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void prepareTests() {
        Address address = Address.builder()
                .firstName("Test")
                .lastName("Testison")
                .phoneNumber("000000000")
                .country("Testland")
                .state("Testshire")
                .city("Test City")
                .postcode("00000")
                .addressLine("Test Street 123")
                .additionalAddressLine("2 C")
                .build();
        Address createdAddress = this.addressRepository.save(address).block();
        if(createdAddress != null) {
            this.id = createdAddress.getId();
        }
    }

    @AfterEach
    void resetTests() {
        this.addressRepository.deleteById(this.id).block();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.addressRepository.findById(id))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }
}
