package com.renewable.wind.service;

import com.renewable.wind.dto.PowerOutputRecordRequest;
import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.entity.EquipmentStatus;
import com.renewable.wind.entity.MaintenanceStatus;
import com.renewable.wind.entity.WindTurbine;
import com.renewable.wind.exception.InvalidOperationException;
import com.renewable.wind.exception.WindTurbineNotFoundException;
import com.renewable.wind.repository.WindTurbineRepository;
import com.renewable.wind.service.impl.WindTurbineServiceImpl;
import com.renewable.wind.util.WindTurbineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WindTurbineServiceImplTest {

    @Mock
    private WindTurbineRepository windTurbineRepository;

    private final WindTurbineMapper windTurbineMapper = new WindTurbineMapper();

    private WindTurbineServiceImpl windTurbineService;

    private WindTurbine sampleTurbine;

    @BeforeEach
    void setUp() {
        windTurbineService = new WindTurbineServiceImpl(windTurbineRepository, windTurbineMapper);
        sampleTurbine = WindTurbine.builder()
                .id(1L)
                .deviceName("Turbine-A1")
                .location("Rooftop-North")
                .capacity(500.0)
                .currentOutput(0.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();
    }

    @Test
    void registerTurbine_shouldSaveAndReturnResponse() {
        WindTurbineCreateRequest request = new WindTurbineCreateRequest(
                "Turbine-A1", "Rooftop-North", 500.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);
        when(windTurbineRepository.save(any(WindTurbine.class))).thenReturn(sampleTurbine);

        WindTurbineResponse response = windTurbineService.registerTurbine(request);

        assertNotNull(response);
        assertEquals("Turbine-A1", response.getDeviceName());
        verify(windTurbineRepository, times(1)).save(any(WindTurbine.class));
    }

    @Test
    void getTurbine_whenNotFound_shouldThrowException() {
        when(windTurbineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WindTurbineNotFoundException.class, () -> windTurbineService.getTurbine(99L));
    }

    @Test
    void getTurbine_whenFound_shouldReturnResponse() {
        when(windTurbineRepository.findById(1L)).thenReturn(Optional.of(sampleTurbine));

        WindTurbineResponse response = windTurbineService.getTurbine(1L);

        assertEquals(1L, response.getId());
        assertEquals("Turbine-A1", response.getDeviceName());
    }

    @Test
    void recordPowerOutput_whenUnderMaintenance_shouldThrowException() {
        sampleTurbine.setMaintenance(MaintenanceStatus.UNDER_MAINTENANCE);
        when(windTurbineRepository.findById(1L)).thenReturn(Optional.of(sampleTurbine));

        PowerOutputRecordRequest request = new PowerOutputRecordRequest(100.0, 12);

        assertThrows(InvalidOperationException.class,
                () -> windTurbineService.recordPowerOutput(1L, request));
    }

    @Test
    void recordPowerOutput_whenExceedsCapacity_shouldThrowException() {
        when(windTurbineRepository.findById(1L)).thenReturn(Optional.of(sampleTurbine));

        PowerOutputRecordRequest request = new PowerOutputRecordRequest(600.0, 12);

        assertThrows(InvalidOperationException.class,
                () -> windTurbineService.recordPowerOutput(1L, request));
    }

    @Test
    void recordPowerOutput_whenValid_shouldUpdateGeneration() {
        when(windTurbineRepository.findById(1L)).thenReturn(Optional.of(sampleTurbine));
        when(windTurbineRepository.save(any(WindTurbine.class))).thenReturn(sampleTurbine);

        PowerOutputRecordRequest request = new PowerOutputRecordRequest(350.0, 12);
        WindTurbineResponse response = windTurbineService.recordPowerOutput(1L, request);

        assertEquals(350.0, response.getCurrentOutput());
    }

    @Test
    void deleteTurbine_whenNotFound_shouldThrowException() {
        when(windTurbineRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(WindTurbineNotFoundException.class, () -> windTurbineService.deleteTurbine(5L));
    }

    @Test
    void getAllTurbines_shouldReturnMappedList() {
        when(windTurbineRepository.findAll()).thenReturn(List.of(sampleTurbine));

        List<WindTurbineResponse> responses = windTurbineService.getAllTurbines();

        assertEquals(1, responses.size());
        assertEquals("Turbine-A1", responses.get(0).getDeviceName());
    }
}
