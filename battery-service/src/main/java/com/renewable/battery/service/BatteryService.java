package com.renewable.battery.service;

import com.renewable.battery.dto.BatteryCreateRequest;
import com.renewable.battery.dto.BatteryResponse;
import com.renewable.battery.dto.BatteryUpdateRequest;
import com.renewable.battery.dto.ChargeRequest;
import com.renewable.battery.dto.DischargeRequest;
import com.renewable.battery.dto.LowBatteryAlertResponse;

import java.util.List;

public interface BatteryService {

    BatteryResponse createBattery(BatteryCreateRequest request);

    BatteryResponse updateBattery(Long id, BatteryUpdateRequest request);

    void deleteBattery(Long id);

    BatteryResponse getBattery(Long id);

    List<BatteryResponse> getAllBatteries();

    BatteryResponse chargeBattery(Long id, ChargeRequest request);

    BatteryResponse dischargeBattery(Long id, DischargeRequest request);

    Double getAvailableCapacity(Long id);

    Double getRemainingCapacity(Long id);

    Double getTotalAvailableCapacity();

    Double getTotalRemainingCapacity();

    List<LowBatteryAlertResponse> getLowBatteryAlerts();
}
