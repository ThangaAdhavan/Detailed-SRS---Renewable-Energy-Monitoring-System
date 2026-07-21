package com.renewable.distribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Local read-only representation of the BatteryResponse returned by
 * battery-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteryDto {
    private Long id;
    private String deviceName;
    private String location;
    private Double capacity;
    private Double chargePercentage;
    private Double availableCapacity;
    private Double remainingCapacity;
    private BatteryStatus status;
    private LocalDateTime createdAt;
    private boolean lowBatteryAlert;
}
