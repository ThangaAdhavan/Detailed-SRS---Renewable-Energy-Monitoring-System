package com.renewable.distribution.client;

import com.renewable.distribution.dto.BatteryDto;
import com.renewable.distribution.dto.ChargeRequestDto;
import com.renewable.distribution.dto.DischargeRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class BatteryServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.battery.base-url}")
    private String batteryBaseUrl;

    public BatteryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BatteryDto> getAllBatteries() {
        ResponseEntity<List<BatteryDto>> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BatteryDto>>() {});
        return response.getBody();
    }

    public Double getTotalAvailableCapacity() {
        ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery/capacity/total-available",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {});
        Map<String, Double> body = response.getBody();
        return body == null ? 0.0 : body.getOrDefault("totalAvailableCapacity", 0.0);
    }

    public Double getTotalRemainingCapacity() {
        ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery/capacity/total-remaining",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {});
        Map<String, Double> body = response.getBody();
        return body == null ? 0.0 : body.getOrDefault("totalRemainingCapacity", 0.0);
    }

    public BatteryDto chargeFirstAvailableBattery(Long batteryId, double energyAmountKwh) {
        HttpEntity<ChargeRequestDto> requestEntity = new HttpEntity<>(new ChargeRequestDto(energyAmountKwh));
        ResponseEntity<BatteryDto> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery/" + batteryId + "/charge",
                HttpMethod.POST,
                requestEntity,
                BatteryDto.class);
        return response.getBody();
    }

    public BatteryDto dischargeBattery(Long batteryId, double energyAmountKwh) {
        HttpEntity<DischargeRequestDto> requestEntity = new HttpEntity<>(new DischargeRequestDto(energyAmountKwh));
        ResponseEntity<BatteryDto> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery/" + batteryId + "/discharge",
                HttpMethod.POST,
                requestEntity,
                BatteryDto.class);
        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLowBatteryAlerts() {
        ResponseEntity<List> response = restTemplate.exchange(
                batteryBaseUrl + "/api/battery/alerts/low-battery",
                HttpMethod.GET,
                null,
                List.class);
        return response.getBody();
    }
}
