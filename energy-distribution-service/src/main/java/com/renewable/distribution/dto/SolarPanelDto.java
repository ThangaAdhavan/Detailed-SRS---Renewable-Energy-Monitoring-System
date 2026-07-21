package com.renewable.distribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Local read-only representation of the SolarPanelResponse returned by
 * solar-panel-service. Kept as a manually maintained mirror DTO (no shared
 * library / no MapStruct) as required by project standards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolarPanelDto {
    private Long id;
    private String deviceName;
    private String location;
    private Double capacity;
    private Double currentGeneration;
    private EquipmentStatus status;
    private MaintenanceStatus maintenance;
    private LocalDateTime createdAt;
}
