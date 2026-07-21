package com.renewable.wind.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI windTurbineServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wind Turbine Service API")
                        .description("REST API for managing wind turbines and recording hourly power output data")
                        .version("1.0.0")
                        .contact(new Contact().name("Renewable Energy Monitoring System")));
    }
}
