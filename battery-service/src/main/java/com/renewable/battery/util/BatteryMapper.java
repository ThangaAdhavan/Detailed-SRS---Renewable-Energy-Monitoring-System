package com.renewable.battery.util;

import com.renewable.battery.dto.BatteryCreateRequest;
import com.renewable.battery.dto.BatteryResponse;
import com.renewable.battery.dto.BatteryUpdateRequest;
import com.renewable.battery.entity.Battery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatteryMapper {

    @Value("${battery.low-threshold-percentage:20.0}")
    private double lowThreshold;

    public Battery toEntity(BatteryCreateRequest request) {
        return Battery.builder()
                .deviceName(request.getDeviceName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .chargePercentage(0.0)
                .status(request.getStatus())
                .build();
    }

    public void updateEntityFromRequest(Battery battery, BatteryUpdateRequest request) {
        battery.setDeviceName(request.getDeviceName());
        battery.setLocation(request.getLocation());
        battery.setCapacity(request.getCapacity());
        battery.setStatus(request.getStatus());
        battery.recalculateCapacities();
    }

    public BatteryResponse toResponse(Battery battery) {
        return BatteryResponse.builder()
                .id(battery.getId())
                .deviceName(battery.getDeviceName())
                .location(battery.getLocation())
                .capacity(battery.getCapacity())
                .chargePercentage(battery.getChargePercentage())
                .availableCapacity(battery.getAvailableCapacity())
                .remainingCapacity(battery.getRemainingCapacity())
                .status(battery.getStatus())
                .createdAt(battery.getCreatedAt())
                .lowBatteryAlert(battery.getChargePercentage() < lowThreshold)
                .build();
    }
}
