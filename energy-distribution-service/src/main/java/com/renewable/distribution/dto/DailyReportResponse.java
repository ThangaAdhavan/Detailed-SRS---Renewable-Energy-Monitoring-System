package com.renewable.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReportResponse {
    private LocalDate reportDate;
    private int totalDistributionEvents;
    private double totalRenewablePowerGenerated;
    private double totalBatteryPowerUsed;
    private double totalPowerDistributed;
    private double totalUnmetDemand;
    private int totalFaultsDetected;
    private List<FaultResponse> faults;
    private List<DistributionResponse> distributions;
}
