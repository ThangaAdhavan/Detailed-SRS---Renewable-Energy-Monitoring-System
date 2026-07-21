package com.renewable.solar.service.impl;

import com.renewable.solar.dto.GenerationRecordRequest;
import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.dto.SolarPanelUpdateRequest;
import com.renewable.solar.entity.MaintenanceStatus;
import com.renewable.solar.entity.SolarPanel;
import com.renewable.solar.exception.InvalidOperationException;
import com.renewable.solar.exception.SolarPanelNotFoundException;
import com.renewable.solar.repository.SolarPanelRepository;
import com.renewable.solar.service.SolarPanelService;
import com.renewable.solar.util.SolarPanelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolarPanelServiceImpl implements SolarPanelService {

    private final SolarPanelRepository solarPanelRepository;
    private final SolarPanelMapper solarPanelMapper;

    public SolarPanelServiceImpl(SolarPanelRepository solarPanelRepository, SolarPanelMapper solarPanelMapper) {
        this.solarPanelRepository = solarPanelRepository;
        this.solarPanelMapper = solarPanelMapper;
    }

    @Override
    @Transactional
    public SolarPanelResponse registerPanel(SolarPanelCreateRequest request) {
        SolarPanel panel = solarPanelMapper.toEntity(request);
        SolarPanel saved = solarPanelRepository.save(panel);
        return solarPanelMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SolarPanelResponse updatePanel(Long id, SolarPanelUpdateRequest request) {
        SolarPanel panel = solarPanelRepository.findById(id)
                .orElseThrow(() -> new SolarPanelNotFoundException(id));
        solarPanelMapper.updateEntityFromRequest(panel, request);
        SolarPanel updated = solarPanelRepository.save(panel);
        return solarPanelMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePanel(Long id) {
        SolarPanel panel = solarPanelRepository.findById(id)
                .orElseThrow(() -> new SolarPanelNotFoundException(id));
        solarPanelRepository.delete(panel);
    }

    @Override
    public SolarPanelResponse getPanel(Long id) {
        SolarPanel panel = solarPanelRepository.findById(id)
                .orElseThrow(() -> new SolarPanelNotFoundException(id));
        return solarPanelMapper.toResponse(panel);
    }

    @Override
    public List<SolarPanelResponse> getAllPanels() {
        return solarPanelRepository.findAll().stream()
                .map(solarPanelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolarPanelResponse recordGeneration(Long id, GenerationRecordRequest request) {
        SolarPanel panel = solarPanelRepository.findById(id)
                .orElseThrow(() -> new SolarPanelNotFoundException(id));

        if (panel.getMaintenance() == MaintenanceStatus.UNDER_MAINTENANCE) {
            throw new InvalidOperationException(
                    "Cannot record generation. Panel " + id + " is under maintenance.");
        }

        if (request.getGenerationValue() > panel.getCapacity()) {
            throw new InvalidOperationException(
                    "Generation value cannot exceed panel capacity of " + panel.getCapacity());
        }

        panel.setCurrentGeneration(request.getGenerationValue());
        SolarPanel updated = solarPanelRepository.save(panel);
        return solarPanelMapper.toResponse(updated);
    }

    @Override
    public Double getTotalCurrentGeneration() {
        Double total = solarPanelRepository.sumCurrentGeneration();
        return total == null ? 0.0 : total;
    }

    @Override
    public List<SolarPanelResponse> getZeroGenerationFaults() {
        return solarPanelRepository.findZeroGenerationOperationalPanels().stream()
                .map(solarPanelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolarPanelResponse> getPanelsUnderMaintenance() {
        return solarPanelRepository.findByMaintenance(MaintenanceStatus.UNDER_MAINTENANCE).stream()
                .map(solarPanelMapper::toResponse)
                .collect(Collectors.toList());
    }
}