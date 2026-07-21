package com.renewable.solar.service;

import com.renewable.solar.dto.GenerationRecordRequest;
import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.dto.SolarPanelUpdateRequest;

import java.util.List;

public interface SolarPanelService {

    SolarPanelResponse registerPanel(SolarPanelCreateRequest request);

    SolarPanelResponse updatePanel(Long id, SolarPanelUpdateRequest request);

    void deletePanel(Long id);

    SolarPanelResponse getPanel(Long id);

    List<SolarPanelResponse> getAllPanels();

    SolarPanelResponse recordGeneration(Long id, GenerationRecordRequest request);

    Double getTotalCurrentGeneration();

    List<SolarPanelResponse> getZeroGenerationFaults();

    List<SolarPanelResponse> getPanelsUnderMaintenance();
}
