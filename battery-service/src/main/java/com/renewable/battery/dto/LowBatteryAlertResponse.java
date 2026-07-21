package com.renewable.battery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowBatteryAlertResponse {

    private Long batteryId;
    private String deviceName;
    private Double chargePercentage;
    private String alertMessage;
}
