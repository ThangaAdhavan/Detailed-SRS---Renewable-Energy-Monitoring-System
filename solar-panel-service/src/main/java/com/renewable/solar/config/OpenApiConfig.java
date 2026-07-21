package com.renewable.solar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solarPanelServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Solar Panel Service API")
                        .description("REST API for managing solar panels and recording hourly generation data")
                        .version("1.0.0")
                        .contact(new Contact().name("Renewable Energy Monitoring System")));
    }
}
