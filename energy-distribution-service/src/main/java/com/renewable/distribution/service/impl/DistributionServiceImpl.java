package com.renewable.distribution.service.impl;

import com.renewable.distribution.client.BatteryServiceClient;
import com.renewable.distribution.client.SolarServiceClient;
import com.renewable.distribution.client.WindServiceClient;
import com.renewable.distribution.dto.*;
import com.renewable.distribution.entity.DeviceType;
import com.renewable.distribution.entity.Distribution;
import com.renewable.distribution.entity.Fault;
import com.renewable.distribution.entity.FaultType;
import com.renewable.distribution.exception.DistributionException;
import com.renewable.distribution.exception.DistributionNotFoundException;
import com.renewable.distribution.exception.FaultDetectionException;
import com.renewable.distribution.repository.DistributionRepository;
import com.renewable.distribution.repository.FaultRepository;
import com.renewable.distribution.service.DistributionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl implements DistributionService {

    private final SolarServiceClient solarServiceClient;
    private final WindServiceClient windServiceClient;
    private final BatteryServiceClient batteryServiceClient;
    private final DistributionRepository distributionRepository;
    private final FaultRepository faultRepository;

    @Value("${distribution.active-hours-start:6}")
    private int activeHoursStart;

    @Value("${distribution.active-hours-end:18}")
    private int activeHoursEnd;

    public DistributionServiceImpl(SolarServiceClient solarServiceClient,
                                    WindServiceClient windServiceClient,
                                    BatteryServiceClient batteryServiceClient,
                                    DistributionRepository distributionRepository,
                                    FaultRepository faultRepository) {
        this.solarServiceClient = solarServiceClient;
        this.windServiceClient = windServiceClient;
        this.batteryServiceClient = batteryServiceClient;
        this.distributionRepository = distributionRepository;
        this.faultRepository = faultRepository;
    }

    @Override
    @Transactional
    public DistributionResponse processDistribution(DistributionRequest request) {
        double demand = request.getDemandKwh();

        // Step 1: Priority 1 - Renewable Energy. Aggregate solar + wind generation via RestTemplate calls.
        double renewablePower;
        try {
            double solarGeneration = solarServiceClient.getTotalGeneration();
            double windOutput = windServiceClient.getTotalOutput();
            renewablePower = solarGeneration + windOutput;
        } catch (RestClientException ex) {
            throw new DistributionException("Failed to aggregate renewable generation: " + ex.getMessage(), ex);
        }

        double distributedPower;
        double batteryPower;
        double remainingPower;

        if (renewablePower >= demand) {
            // Renewable energy alone satisfies demand. Store the excess in battery storage.
            distributedPower = demand;
            double excess = renewablePower - demand;
            double stored = storeExcessEnergy(excess);
            batteryPower = -stored; // negative denotes energy charged into storage
            remainingPower = 0.0;
        } else {
            // Priority 2 - Battery: cover the shortfall from stored battery energy.
            double gap = demand - renewablePower;
            double discharged = dischargeFromBatteries(gap);
            distributedPower = renewablePower + discharged;
            batteryPower = discharged; // positive denotes energy discharged to meet demand
            // Priority 3 - Remaining demand that could not be satisfied by renewables + battery.
            remainingPower = gap - discharged;
        }

        Distribution distribution = Distribution.builder()
                .requestedDemand(demand)
                .renewablePower(round2(renewablePower))
                .batteryPower(round2(batteryPower))
                .distributedPower(round2(distributedPower))
                .remainingPower(round2(Math.max(remainingPower, 0.0)))
                .build();

        Distribution saved = distributionRepository.save(distribution);
        return toResponse(saved);
    }

    /**
     * Stores excess renewable energy in available battery capacity, only up to the
     * total available capacity across all non-faulty batteries. Distributes the
     * excess across batteries in the order returned by the battery service.
     */
    private double storeExcessEnergy(double excess) {
        if (excess <= 0) {
            return 0.0;
        }
        List<BatteryDto> batteries = safeList(batteryServiceClient::getAllBatteries);
        double remainingToStore = excess;
        double totalStored = 0.0;

        for (BatteryDto battery : batteries) {
            if (remainingToStore <= 0) {
                break;
            }
            if (battery.getStatus() == BatteryStatus.FAULT) {
                continue;
            }
            double available = battery.getAvailableCapacity() == null ? 0.0 : battery.getAvailableCapacity();
            if (available <= 0) {
                continue;
            }
            double toCharge = Math.min(available, remainingToStore);
            try {
                batteryServiceClient.chargeFirstAvailableBattery(battery.getId(), toCharge);
                totalStored += toCharge;
                remainingToStore -= toCharge;
            } catch (RestClientException ex) {
                // Skip this battery and continue trying others; excess simply goes unstored if all fail.
            }
        }
        return totalStored;
    }

    /**
     * Discharges stored battery energy to cover a shortfall in renewable generation,
     * only up to each battery's remaining stored capacity.
     */
    private double dischargeFromBatteries(double gap) {
        if (gap <= 0) {
            return 0.0;
        }
        List<BatteryDto> batteries = safeList(batteryServiceClient::getAllBatteries);
        double remainingGap = gap;
        double totalDischarged = 0.0;

        for (BatteryDto battery : batteries) {
            if (remainingGap <= 0) {
                break;
            }
            if (battery.getStatus() == BatteryStatus.FAULT) {
                continue;
            }
            double remaining = battery.getRemainingCapacity() == null ? 0.0 : battery.getRemainingCapacity();
            if (remaining <= 0) {
                continue;
            }
            double toDischarge = Math.min(remaining, remainingGap);
            try {
                batteryServiceClient.dischargeBattery(battery.getId(), toDischarge);
                totalDischarged += toDischarge;
                remainingGap -= toDischarge;
            } catch (RestClientException ex) {
                // Skip this battery and continue trying others.
            }
        }
        return totalDischarged;
    }

    @Override
    public DistributionResponse getDistribution(Long id) {
        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new DistributionNotFoundException(id));
        return toResponse(distribution);
    }

    @Override
    public List<DistributionResponse> getAllDistributions() {
        return distributionRepository.findAllByOrderByDistributionDateDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FaultResponse> detectFaults() {
        List<Fault> newFaults = new ArrayList<>();
        int currentHour = LocalTime.now().getHour();
        boolean isActiveHours = currentHour >= activeHoursStart && currentHour < activeHoursEnd;

        try {
            if (isActiveHours) {
                for (SolarPanelDto panel : safeList(solarServiceClient::getZeroGenerationFaults)) {
                    newFaults.add(buildFault(DeviceType.SOLAR_PANEL, panel.getId(), FaultType.ZERO_GENERATION,
                            "Solar panel " + panel.getDeviceName() + " produced zero generation during active hours"));
                }
                for (WindTurbineDto turbine : safeList(windServiceClient::getZeroOutputFaults)) {
                    newFaults.add(buildFault(DeviceType.WIND_TURBINE, turbine.getId(), FaultType.ZERO_GENERATION,
                            "Wind turbine " + turbine.getDeviceName() + " produced zero output during active hours"));
                }
            }

            for (Map<String, Object> alert : safeList(batteryServiceClient::getLowBatteryAlerts)) {
                Object batteryId = alert.get("batteryId");
                Object deviceName = alert.get("deviceName");
                Long id = batteryId == null ? null : Long.valueOf(String.valueOf(batteryId));
                newFaults.add(buildFault(DeviceType.BATTERY, id, FaultType.LOW_BATTERY,
                        "Battery " + deviceName + " is below the low-charge threshold"));
            }

            for (SolarPanelDto panel : safeList(solarServiceClient::getPanelsUnderMaintenance)) {
                newFaults.add(buildFault(DeviceType.SOLAR_PANEL, panel.getId(), FaultType.UNDER_MAINTENANCE,
                        "Solar panel " + panel.getDeviceName() + " is under maintenance and cannot participate in distribution"));
            }
            for (WindTurbineDto turbine : safeList(windServiceClient::getTurbinesUnderMaintenance)) {
                newFaults.add(buildFault(DeviceType.WIND_TURBINE, turbine.getId(), FaultType.UNDER_MAINTENANCE,
                        "Wind turbine " + turbine.getDeviceName() + " is under maintenance and cannot participate in distribution"));
            }
        } catch (RestClientException ex) {
            throw new FaultDetectionException("Failed to run fault detection: " + ex.getMessage(), ex);
        }

        List<Fault> saved = faultRepository.saveAll(newFaults);
        return saved.stream().map(this::toFaultResponse).collect(Collectors.toList());
    }

    @Override
    public List<FaultResponse> getFaultHistory() {
        return faultRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toFaultResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DailyReportResponse getDailyReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Distribution> distributions = distributionRepository.findByDistributionDateBetween(start, end);
        List<Fault> faults = faultRepository.findByCreatedAtBetween(start, end);

        double totalRenewable = distributions.stream().mapToDouble(Distribution::getRenewablePower).sum();
        double totalBattery = distributions.stream().mapToDouble(d -> Math.max(d.getBatteryPower(), 0.0)).sum();
        double totalDistributed = distributions.stream().mapToDouble(Distribution::getDistributedPower).sum();
        double totalUnmet = distributions.stream().mapToDouble(Distribution::getRemainingPower).sum();

        return DailyReportResponse.builder()
                .reportDate(date)
                .totalDistributionEvents(distributions.size())
                .totalRenewablePowerGenerated(round2(totalRenewable))
                .totalBatteryPowerUsed(round2(totalBattery))
                .totalPowerDistributed(round2(totalDistributed))
                .totalUnmetDemand(round2(totalUnmet))
                .totalFaultsDetected(faults.size())
                .faults(faults.stream().map(this::toFaultResponse).collect(Collectors.toList()))
                .distributions(distributions.stream().map(this::toResponse).collect(Collectors.toList()))
                .build();
    }

    private Fault buildFault(DeviceType deviceType, Long deviceId, FaultType faultType, String description) {
        return Fault.builder()
                .deviceType(deviceType)
                .deviceId(deviceId)
                .faultType(faultType)
                .description(description)
                .build();
    }

    private DistributionResponse toResponse(Distribution distribution) {
        return DistributionResponse.builder()
                .id(distribution.getId())
                .requestedDemand(distribution.getRequestedDemand())
                .renewablePower(distribution.getRenewablePower())
                .batteryPower(distribution.getBatteryPower())
                .distributedPower(distribution.getDistributedPower())
                .remainingPower(distribution.getRemainingPower())
                .distributionDate(distribution.getDistributionDate())
                .build();
    }

    private FaultResponse toFaultResponse(Fault fault) {
        return FaultResponse.builder()
                .id(fault.getId())
                .deviceType(fault.getDeviceType())
                .deviceId(fault.getDeviceId())
                .faultType(fault.getFaultType())
                .description(fault.getDescription())
                .createdAt(fault.getCreatedAt())
                .build();
    }

    private <T> List<T> safeList(java.util.function.Supplier<List<T>> supplier) {
        List<T> result = supplier.get();
        return result == null ? List.of() : result;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
