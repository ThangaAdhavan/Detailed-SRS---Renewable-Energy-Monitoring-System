package com.renewable.wind.dto;

import com.renewable.wind.entity.EquipmentStatus;
import com.renewable.wind.entity.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WindTurbineResponse {

    private Long id;
    private String deviceName;
    private String location;
    private Double capacity;
    private Double currentOutput;
    private EquipmentStatus status;
    private MaintenanceStatus maintenance;
    private LocalDateTime createdAt;
}
