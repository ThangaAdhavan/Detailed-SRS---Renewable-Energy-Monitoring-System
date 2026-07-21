package com.renewable.solar.dto;

import com.renewable.solar.entity.EquipmentStatus;
import com.renewable.solar.entity.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanelResponse {

    private Long id;
    private String deviceName;
    private String location;
    private Double capacity;
    private Double currentGeneration;
    private EquipmentStatus status;
    private MaintenanceStatus maintenance;
    private LocalDateTime createdAt;
}
