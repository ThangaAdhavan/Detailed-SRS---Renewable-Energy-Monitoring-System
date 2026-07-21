package com.renewable.solar.dto;

import com.renewable.solar.entity.EquipmentStatus;
import com.renewable.solar.entity.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolarPanelCreateRequest {

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Double capacity;

    @NotNull(message = "Equipment status is required")
    private EquipmentStatus status;

    @NotNull(message = "Maintenance status is required")
    private MaintenanceStatus maintenance;
}
