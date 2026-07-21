package com.renewable.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistributionResponse {
    private Long id;
    private Double requestedDemand;
    private Double renewablePower;
    private Double batteryPower;
    private Double distributedPower;
    private Double remainingPower;
    private LocalDateTime distributionDate;
}
