package com.laiacano.core.config;

import com.laiacano.core.utils.RoutingUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
    private final String activeProfile;
    public UtilsConfig(@Value("${spring.profiles.active}") String activeProfile) {
        this.activeProfile = activeProfile;
    }
    @Bean
    public RoutingUtils routingUtils() {
        return new RoutingUtils(activeProfile);
    }
}
