package com.renewable.battery.service.impl;

import com.renewable.battery.dto.*;
import com.renewable.battery.entity.Battery;
import com.renewable.battery.entity.BatteryStatus;
import com.renewable.battery.exception.BatteryNotFoundException;
import com.renewable.battery.exception.InsufficientCapacityException;
import com.renewable.battery.exception.InvalidOperationException;
import com.renewable.battery.repository.BatteryRepository;
import com.renewable.battery.service.BatteryService;
import com.renewable.battery.util.BatteryMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatteryServiceImpl implements BatteryService {

    private final BatteryRepository batteryRepository;
    private final BatteryMapper batteryMapper;

    @Value("${battery.low-threshold-percentage:20.0}")
    private double lowThreshold;

    public BatteryServiceImpl(BatteryRepository batteryRepository, BatteryMapper batteryMapper) {
        this.batteryRepository = batteryRepository;
        this.batteryMapper = batteryMapper;
    }

    @Override
    @Transactional
    public BatteryResponse createBattery(BatteryCreateRequest request) {
        Battery battery = batteryMapper.toEntity(request);
        Battery saved = batteryRepository.save(battery);
        return batteryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BatteryResponse updateBattery(Long id, BatteryUpdateRequest request) {
        Battery battery = findBatteryOrThrow(id);
        batteryMapper.updateEntityFromRequest(battery, request);
        Battery updated = batteryRepository.save(battery);
        return batteryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBattery(Long id) {
        Battery battery = findBatteryOrThrow(id);
        batteryRepository.delete(battery);
    }

    @Override
    public BatteryResponse getBattery(Long id) {
        return batteryMapper.toResponse(findBatteryOrThrow(id));
    }

    @Override
    public List<BatteryResponse> getAllBatteries() {
        return batteryRepository.findAll().stream()
                .map(batteryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BatteryResponse chargeBattery(Long id, ChargeRequest request) {
        Battery battery = findBatteryOrThrow(id);

        if (battery.getStatus() == BatteryStatus.FAULT) {
            throw new InvalidOperationException("Cannot charge battery " + id + " - equipment is in FAULT status.");
        }

        double currentEnergyKwh = (battery.getChargePercentage() / 100.0) * battery.getCapacity();
        double availableRoom = battery.getCapacity() - currentEnergyKwh;

        if (request.getEnergyAmountKwh() > availableRoom) {
            // Store excess energy only up to capacity; cap at 100%.
            currentEnergyKwh = battery.getCapacity();
        } else {
            currentEnergyKwh += request.getEnergyAmountKwh();
        }

        double newPercentage = (currentEnergyKwh / battery.getCapacity()) * 100.0;
        newPercentage = clampPercentage(newPercentage);

        battery.setChargePercentage(newPercentage);
        battery.recalculateCapacities();

        if (battery.getChargePercentage() > lowThreshold && battery.getStatus() == BatteryStatus.LOW_BATTERY) {
            battery.setStatus(BatteryStatus.ACTIVE);
        }

        Battery updated = batteryRepository.save(battery);
        return batteryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public BatteryResponse dischargeBattery(Long id, DischargeRequest request) {
        Battery battery = findBatteryOrThrow(id);

        if (battery.getStatus() == BatteryStatus.FAULT) {
            throw new InvalidOperationException("Cannot discharge battery " + id + " - equipment is in FAULT status.");
        }

        double currentEnergyKwh = (battery.getChargePercentage() / 100.0) * battery.getCapacity();

        if (request.getEnergyAmountKwh() > currentEnergyKwh) {
            throw new InsufficientCapacityException(
                    "Insufficient stored energy in battery " + id + ". Available: " + round2(currentEnergyKwh) + " kWh");
        }

        currentEnergyKwh -= request.getEnergyAmountKwh();
        double newPercentage = clampPercentage((currentEnergyKwh / battery.getCapacity()) * 100.0);

        battery.setChargePercentage(newPercentage);
        battery.recalculateCapacities();

        if (battery.getChargePercentage() < lowThreshold) {
            battery.setStatus(BatteryStatus.LOW_BATTERY);
        }

        Battery updated = batteryRepository.save(battery);
        return batteryMapper.toResponse(updated);
    }

    @Override
    public Double getAvailableCapacity(Long id) {
        return findBatteryOrThrow(id).getAvailableCapacity();
    }

    @Override
    public Double getRemainingCapacity(Long id) {
        return findBatteryOrThrow(id).getRemainingCapacity();
    }

    @Override
    public Double getTotalAvailableCapacity() {
        Double total = batteryRepository.sumAvailableCapacity();
        return total == null ? 0.0 : total;
    }

    @Override
    public Double getTotalRemainingCapacity() {
        Double total = batteryRepository.sumRemainingCapacity();
        return total == null ? 0.0 : total;
    }

    @Override
    public List<LowBatteryAlertResponse> getLowBatteryAlerts() {
        return batteryRepository.findBatteriesBelowThreshold(lowThreshold).stream()
                .map(battery -> LowBatteryAlertResponse.builder()
                        .batteryId(battery.getId())
                        .deviceName(battery.getDeviceName())
                        .chargePercentage(battery.getChargePercentage())
                        .alertMessage("LOW BATTERY ALERT: " + battery.getDeviceName()
                                + " is at " + battery.getChargePercentage() + "% charge")
                        .build())
                .collect(Collectors.toList());
    }

    private Battery findBatteryOrThrow(Long id) {
        return batteryRepository.findById(id)
                .orElseThrow(() -> new BatteryNotFoundException(id));
    }

    private double clampPercentage(double value) {
        if (value > 100.0) return 100.0;
        if (value < 0.0) return 0.0;
        return value;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
