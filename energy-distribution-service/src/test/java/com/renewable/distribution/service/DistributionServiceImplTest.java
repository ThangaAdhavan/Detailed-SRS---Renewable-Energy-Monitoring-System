package com.renewable.distribution.service;

import com.renewable.distribution.client.BatteryServiceClient;
import com.renewable.distribution.client.SolarServiceClient;
import com.renewable.distribution.client.WindServiceClient;
import com.renewable.distribution.dto.BatteryDto;
import com.renewable.distribution.dto.BatteryStatus;
import com.renewable.distribution.dto.DistributionRequest;
import com.renewable.distribution.dto.DistributionResponse;
import com.renewable.distribution.repository.DistributionRepository;
import com.renewable.distribution.repository.FaultRepository;
import com.renewable.distribution.entity.Distribution;
import com.renewable.distribution.service.impl.DistributionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributionServiceImplTest {

    @Mock
    private SolarServiceClient solarServiceClient;

    @Mock
    private WindServiceClient windServiceClient;

    @Mock
    private BatteryServiceClient batteryServiceClient;

    @Mock
    private DistributionRepository distributionRepository;

    @Mock
    private FaultRepository faultRepository;

    private DistributionServiceImpl distributionService;

    @BeforeEach
    void setUp() {
        distributionService = new DistributionServiceImpl(
                solarServiceClient, windServiceClient, batteryServiceClient, distributionRepository, faultRepository);
        ReflectionTestUtils.setField(distributionService, "activeHoursStart", 6);
        ReflectionTestUtils.setField(distributionService, "activeHoursEnd", 18);
    }

    @Test
    void processDistribution_whenRenewableExceedsDemand_shouldStoreExcessInBattery() {
        when(solarServiceClient.getTotalGeneration()).thenReturn(300.0);
        when(windServiceClient.getTotalOutput()).thenReturn(200.0);

        BatteryDto battery = new BatteryDto(1L, "Battery-1", "Hall-A", 100.0, 50.0, 50.0, 50.0, BatteryStatus.ACTIVE, null, false);
        when(batteryServiceClient.getAllBatteries()).thenReturn(List.of(battery));
        when(batteryServiceClient.chargeFirstAvailableBattery(1L, 50.0)).thenReturn(battery);

        when(distributionRepository.save(any(Distribution.class))).thenAnswer(inv -> {
            Distribution d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        DistributionResponse response = distributionService.processDistribution(new DistributionRequest(450.0));

        assertEquals(450.0, response.getDistributedPower(), 0.01);
        assertEquals(0.0, response.getRemainingPower(), 0.01);
        assertEquals(500.0, response.getRenewablePower(), 0.01);
    }

    @Test
    void processDistribution_whenRenewableInsufficientAndBatteryCoversGap_shouldMeetDemand() {
        when(solarServiceClient.getTotalGeneration()).thenReturn(100.0);
        when(windServiceClient.getTotalOutput()).thenReturn(50.0);

        BatteryDto battery = new BatteryDto(1L, "Battery-1", "Hall-A", 100.0, 80.0, 20.0, 80.0, BatteryStatus.ACTIVE, null, false);
        when(batteryServiceClient.getAllBatteries()).thenReturn(List.of(battery));
        when(batteryServiceClient.dischargeBattery(1L, 50.0)).thenReturn(battery);

        when(distributionRepository.save(any(Distribution.class))).thenAnswer(inv -> {
            Distribution d = inv.getArgument(0);
            d.setId(2L);
            return d;
        });

        DistributionResponse response = distributionService.processDistribution(new DistributionRequest(200.0));

        assertEquals(200.0, response.getDistributedPower(), 0.01);
        assertEquals(0.0, response.getRemainingPower(), 0.01);
        assertEquals(50.0, response.getBatteryPower(), 0.01);
    }

    @Test
    void processDistribution_whenRenewableAndBatteryInsufficient_shouldReportRemainingPower() {
        when(solarServiceClient.getTotalGeneration()).thenReturn(50.0);
        when(windServiceClient.getTotalOutput()).thenReturn(20.0);
        when(batteryServiceClient.getAllBatteries()).thenReturn(List.of());

        when(distributionRepository.save(any(Distribution.class))).thenAnswer(inv -> {
            Distribution d = inv.getArgument(0);
            d.setId(3L);
            return d;
        });

        DistributionResponse response = distributionService.processDistribution(new DistributionRequest(200.0));

        assertEquals(70.0, response.getDistributedPower(), 0.01);
        assertEquals(130.0, response.getRemainingPower(), 0.01);
    }
}
