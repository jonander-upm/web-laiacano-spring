package com.laiacano.core.rest;


import com.laiacano.core.config.jwt.JwtTokenProvider;
import com.laiacano.core.data.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

@Service
public class RestClientTestService {
    private final JwtTokenProvider jwtTokenProvider;
    private String token;

    @Autowired
    public RestClientTestService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private boolean isRole(Role role) {
        return this.token != null && jwtTokenProvider.getRole(this.token).equals(role);
    }

    private WebTestClient login(Role role, String user, String name, WebTestClient webTestClient) {
        if (!this.isRole(role)) {
            this.token = jwtTokenProvider.createToken(user, name, role);

        }
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token).build();
    }

    public WebTestClient loginManager(WebTestClient webTestClient) {
        return this.login(Role.MANAGER, "manager", "manager", webTestClient);
    }

    public WebTestClient loginCustomer(WebTestClient webTestClient) {
        return this.login(Role.CUSTOMER, "customer", "customer", webTestClient);
    }

    public void logout() {
        this.token = null;
    }
}
