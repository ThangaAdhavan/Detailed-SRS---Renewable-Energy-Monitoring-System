package com.renewable.distribution.client;

import com.renewable.distribution.dto.SolarPanelDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class SolarServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.solar.base-url}")
    private String solarBaseUrl;

    public SolarServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SolarPanelDto> getAllPanels() {
        ResponseEntity<List<SolarPanelDto>> response = restTemplate.exchange(
                solarBaseUrl + "/api/solar",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SolarPanelDto>>() {});
        return response.getBody();
    }

    public Double getTotalGeneration() {
        ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                solarBaseUrl + "/api/solar/generation/total",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {});
        Map<String, Double> body = response.getBody();
        return body == null ? 0.0 : body.getOrDefault("totalGeneration", 0.0);
    }

    public List<SolarPanelDto> getZeroGenerationFaults() {
        ResponseEntity<List<SolarPanelDto>> response = restTemplate.exchange(
                solarBaseUrl + "/api/solar/faults/zero-generation",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SolarPanelDto>>() {});
        return response.getBody();
    }

    public List<SolarPanelDto> getPanelsUnderMaintenance() {
        ResponseEntity<List<SolarPanelDto>> response = restTemplate.exchange(
                solarBaseUrl + "/api/solar/maintenance",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SolarPanelDto>>() {});
        return response.getBody();
    }
}
