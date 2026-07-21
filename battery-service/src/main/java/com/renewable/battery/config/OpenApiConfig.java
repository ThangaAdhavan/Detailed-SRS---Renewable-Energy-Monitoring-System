package com.renewable.battery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI batteryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Battery Service API")
                        .description("REST API for managing battery storage units, charging, discharging, and low battery alerts")
                        .version("1.0.0")
                        .contact(new Contact().name("Renewable Energy Monitoring System")));
    }
}
