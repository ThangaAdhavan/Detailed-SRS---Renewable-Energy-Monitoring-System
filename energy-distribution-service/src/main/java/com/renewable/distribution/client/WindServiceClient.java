package com.renewable.distribution.client;

import com.renewable.distribution.dto.WindTurbineDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class WindServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.wind.base-url}")
    private String windBaseUrl;

    public WindServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<WindTurbineDto> getAllTurbines() {
        ResponseEntity<List<WindTurbineDto>> response = restTemplate.exchange(
                windBaseUrl + "/api/wind",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WindTurbineDto>>() {});
        return response.getBody();
    }

    public Double getTotalOutput() {
        ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                windBaseUrl + "/api/wind/power-output/total",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {});
        Map<String, Double> body = response.getBody();
        return body == null ? 0.0 : body.getOrDefault("totalOutput", 0.0);
    }

    public List<WindTurbineDto> getZeroOutputFaults() {
        ResponseEntity<List<WindTurbineDto>> response = restTemplate.exchange(
                windBaseUrl + "/api/wind/faults/zero-output",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WindTurbineDto>>() {});
        return response.getBody();
    }

    public List<WindTurbineDto> getTurbinesUnderMaintenance() {
        ResponseEntity<List<WindTurbineDto>> response = restTemplate.exchange(
                windBaseUrl + "/api/wind/maintenance",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WindTurbineDto>>() {});
        return response.getBody();
    }
}
