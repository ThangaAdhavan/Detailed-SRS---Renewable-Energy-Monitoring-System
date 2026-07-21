package com.renewable.battery.dto;

import com.renewable.battery.entity.BatteryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryResponse {

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
