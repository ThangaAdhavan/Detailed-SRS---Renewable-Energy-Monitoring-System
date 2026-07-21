package com.renewable.distribution.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI energyDistributionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Energy Distribution Service API")
                        .description("REST API for aggregating renewable generation, coordinating battery storage, "
                                + "distributing energy, detecting equipment faults, and generating reports")
                        .version("1.0.0")
                        .contact(new Contact().name("Renewable Energy Monitoring System")));
    }
}
