package com.laiacano.core.data.entities;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    private String id;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String country;
    @NonNull
    private String state;
    @NonNull
    private String city;
    @NonNull
    private String addressLine;
    private String additionalAddressLine;
    @NonNull
    private String postcode;
    @NonNull
    private String phoneNumber;
}
