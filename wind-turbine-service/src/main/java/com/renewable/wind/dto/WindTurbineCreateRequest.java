package com.renewable.wind.dto;

import com.renewable.wind.entity.EquipmentStatus;
import com.renewable.wind.entity.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WindTurbineCreateRequest {

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
