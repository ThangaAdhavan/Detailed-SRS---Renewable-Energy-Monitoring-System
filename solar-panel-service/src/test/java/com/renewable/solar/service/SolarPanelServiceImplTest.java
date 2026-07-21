package com.renewable.solar.service;

import com.renewable.solar.dto.GenerationRecordRequest;
import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.entity.EquipmentStatus;
import com.renewable.solar.entity.MaintenanceStatus;
import com.renewable.solar.entity.SolarPanel;
import com.renewable.solar.exception.InvalidOperationException;
import com.renewable.solar.exception.SolarPanelNotFoundException;
import com.renewable.solar.repository.SolarPanelRepository;
import com.renewable.solar.service.impl.SolarPanelServiceImpl;
import com.renewable.solar.util.SolarPanelMapper;
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
class SolarPanelServiceImplTest {

    @Mock
    private SolarPanelRepository solarPanelRepository;

    private final SolarPanelMapper solarPanelMapper = new SolarPanelMapper();

    private SolarPanelServiceImpl solarPanelService;

    private SolarPanel samplePanel;

    @BeforeEach
    void setUp() {
        solarPanelService = new SolarPanelServiceImpl(solarPanelRepository, solarPanelMapper);
        samplePanel = SolarPanel.builder()
                .id(1L)
                .deviceName("Panel-A1")
                .location("Rooftop-North")
                .capacity(500.0)
                .currentGeneration(0.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();
    }

    @Test
    void registerPanel_shouldSaveAndReturnResponse() {
        SolarPanelCreateRequest request = new SolarPanelCreateRequest(
                "Panel-A1", "Rooftop-North", 500.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);
        when(solarPanelRepository.save(any(SolarPanel.class))).thenReturn(samplePanel);

        SolarPanelResponse response = solarPanelService.registerPanel(request);

        assertNotNull(response);
        assertEquals("Panel-A1", response.getDeviceName());
        verify(solarPanelRepository, times(1)).save(any(SolarPanel.class));
    }

    @Test
    void getPanel_whenNotFound_shouldThrowException() {
        when(solarPanelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SolarPanelNotFoundException.class, () -> solarPanelService.getPanel(99L));
    }

    @Test
    void getPanel_whenFound_shouldReturnResponse() {
        when(solarPanelRepository.findById(1L)).thenReturn(Optional.of(samplePanel));

        SolarPanelResponse response = solarPanelService.getPanel(1L);

        assertEquals(1L, response.getId());
        assertEquals("Panel-A1", response.getDeviceName());
    }

    @Test
    void recordGeneration_whenUnderMaintenance_shouldThrowException() {
        samplePanel.setMaintenance(MaintenanceStatus.UNDER_MAINTENANCE);
        when(solarPanelRepository.findById(1L)).thenReturn(Optional.of(samplePanel));

        GenerationRecordRequest request = new GenerationRecordRequest(100.0, 12);

        assertThrows(InvalidOperationException.class,
                () -> solarPanelService.recordGeneration(1L, request));
    }

    @Test
    void recordGeneration_whenExceedsCapacity_shouldThrowException() {
        when(solarPanelRepository.findById(1L)).thenReturn(Optional.of(samplePanel));

        GenerationRecordRequest request = new GenerationRecordRequest(600.0, 12);

        assertThrows(InvalidOperationException.class,
                () -> solarPanelService.recordGeneration(1L, request));
    }

    @Test
    void recordGeneration_whenValid_shouldUpdateGeneration() {
        when(solarPanelRepository.findById(1L)).thenReturn(Optional.of(samplePanel));
        when(solarPanelRepository.save(any(SolarPanel.class))).thenReturn(samplePanel);

        GenerationRecordRequest request = new GenerationRecordRequest(350.0, 12);
        SolarPanelResponse response = solarPanelService.recordGeneration(1L, request);

        assertEquals(350.0, response.getCurrentGeneration());
    }

    @Test
    void deletePanel_whenNotFound_shouldThrowException() {
        when(solarPanelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(SolarPanelNotFoundException.class, () -> solarPanelService.deletePanel(5L));
    }

    @Test
    void getAllPanels_shouldReturnMappedList() {
        when(solarPanelRepository.findAll()).thenReturn(List.of(samplePanel));

        List<SolarPanelResponse> responses = solarPanelService.getAllPanels();

        assertEquals(1, responses.size());
        assertEquals("Panel-A1", responses.get(0).getDeviceName());
    }
}
