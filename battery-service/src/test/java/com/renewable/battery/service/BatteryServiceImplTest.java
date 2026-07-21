package com.renewable.battery.service;

import com.renewable.battery.dto.BatteryResponse;
import com.renewable.battery.dto.ChargeRequest;
import com.renewable.battery.dto.DischargeRequest;
import com.renewable.battery.entity.Battery;
import com.renewable.battery.entity.BatteryStatus;
import com.renewable.battery.exception.BatteryNotFoundException;
import com.renewable.battery.exception.InsufficientCapacityException;
import com.renewable.battery.repository.BatteryRepository;
import com.renewable.battery.service.impl.BatteryServiceImpl;
import com.renewable.battery.util.BatteryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatteryServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;

    private BatteryMapper batteryMapper;
    private BatteryServiceImpl batteryService;
    private Battery sampleBattery;

    @BeforeEach
    void setUp() {
        batteryMapper = new BatteryMapper();
        ReflectionTestUtils.setField(batteryMapper, "lowThreshold", 20.0);

        batteryService = new BatteryServiceImpl(batteryRepository, batteryMapper);
        ReflectionTestUtils.setField(batteryService, "lowThreshold", 20.0);

        sampleBattery = Battery.builder()
                .id(1L)
                .deviceName("Battery-1")
                .location("Storage-Hall-A")
                .capacity(100.0)
                .chargePercentage(50.0)
                .status(BatteryStatus.ACTIVE)
                .build();
        sampleBattery.recalculateCapacities();
    }

    @Test
    void chargeBattery_shouldIncreasePercentage() {
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));
        when(batteryRepository.save(any(Battery.class))).thenAnswer(inv -> inv.getArgument(0));

        BatteryResponse response = batteryService.chargeBattery(1L, new ChargeRequest(20.0));

        assertEquals(70.0, response.getChargePercentage(), 0.01);
    }

    @Test
    void chargeBattery_shouldCapAt100Percent() {
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));
        when(batteryRepository.save(any(Battery.class))).thenAnswer(inv -> inv.getArgument(0));

        BatteryResponse response = batteryService.chargeBattery(1L, new ChargeRequest(1000.0));

        assertEquals(100.0, response.getChargePercentage(), 0.01);
    }

    @Test
    void dischargeBattery_shouldDecreasePercentage() {
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));
        when(batteryRepository.save(any(Battery.class))).thenAnswer(inv -> inv.getArgument(0));

        BatteryResponse response = batteryService.dischargeBattery(1L, new DischargeRequest(30.0));

        assertEquals(20.0, response.getChargePercentage(), 0.01);
    }

    @Test
    void dischargeBattery_belowThreshold_shouldMarkLowBattery() {
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));
        when(batteryRepository.save(any(Battery.class))).thenAnswer(inv -> inv.getArgument(0));

        BatteryResponse response = batteryService.dischargeBattery(1L, new DischargeRequest(40.0));

        assertEquals(10.0, response.getChargePercentage(), 0.01);
        assertTrue(response.isLowBatteryAlert());
    }

    @Test
    void dischargeBattery_whenInsufficientEnergy_shouldThrow() {
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));

        assertThrows(InsufficientCapacityException.class,
                () -> batteryService.dischargeBattery(1L, new DischargeRequest(90.0)));
    }

    @Test
    void getBattery_whenNotFound_shouldThrow() {
        when(batteryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BatteryNotFoundException.class, () -> batteryService.getBattery(99L));
    }

    @Test
    void chargePercentage_shouldNeverExceed100OrGoBelow0() {
        sampleBattery.setChargePercentage(0.0);
        sampleBattery.recalculateCapacities();
        when(batteryRepository.findById(1L)).thenReturn(Optional.of(sampleBattery));

        assertThrows(InsufficientCapacityException.class,
                () -> batteryService.dischargeBattery(1L, new DischargeRequest(10.0)));
    }
}
