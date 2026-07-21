package com.renewable.distribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Local read-only representation of the WindTurbineResponse returned by
 * wind-turbine-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindTurbineDto {
    private Long id;
    private String deviceName;
    private String location;
    private Double capacity;
    private Double currentOutput;
    private EquipmentStatus status;
    private MaintenanceStatus maintenance;
    private LocalDateTime createdAt;
}
