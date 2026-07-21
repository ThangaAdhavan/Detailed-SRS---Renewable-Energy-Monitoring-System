package com.renewable.wind.service.impl;

import com.renewable.wind.dto.PowerOutputRecordRequest;
import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.dto.WindTurbineUpdateRequest;
import com.renewable.wind.entity.MaintenanceStatus;
import com.renewable.wind.entity.WindTurbine;
import com.renewable.wind.exception.InvalidOperationException;
import com.renewable.wind.exception.WindTurbineNotFoundException;
import com.renewable.wind.repository.WindTurbineRepository;
import com.renewable.wind.service.WindTurbineService;
import com.renewable.wind.util.WindTurbineMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WindTurbineServiceImpl implements WindTurbineService {

    private final WindTurbineRepository windTurbineRepository;
    private final WindTurbineMapper windTurbineMapper;

    public WindTurbineServiceImpl(WindTurbineRepository windTurbineRepository, WindTurbineMapper windTurbineMapper) {
        this.windTurbineRepository = windTurbineRepository;
        this.windTurbineMapper = windTurbineMapper;
    }

    @Override
    @Transactional
    public WindTurbineResponse registerTurbine(WindTurbineCreateRequest request) {
        WindTurbine turbine = windTurbineMapper.toEntity(request);
        WindTurbine saved = windTurbineRepository.save(turbine);
        return windTurbineMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WindTurbineResponse updateTurbine(Long id, WindTurbineUpdateRequest request) {
        WindTurbine turbine = windTurbineRepository.findById(id)
                .orElseThrow(() -> new WindTurbineNotFoundException(id));
        windTurbineMapper.updateEntityFromRequest(turbine, request);
        WindTurbine updated = windTurbineRepository.save(turbine);
        return windTurbineMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTurbine(Long id) {
        WindTurbine turbine = windTurbineRepository.findById(id)
                .orElseThrow(() -> new WindTurbineNotFoundException(id));
        windTurbineRepository.delete(turbine);
    }

    @Override
    public WindTurbineResponse getTurbine(Long id) {
        WindTurbine turbine = windTurbineRepository.findById(id)
                .orElseThrow(() -> new WindTurbineNotFoundException(id));
        return windTurbineMapper.toResponse(turbine);
    }

    @Override
    public List<WindTurbineResponse> getAllTurbines() {
        return windTurbineRepository.findAll().stream()
                .map(windTurbineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WindTurbineResponse recordPowerOutput(Long id, PowerOutputRecordRequest request) {
        WindTurbine turbine = windTurbineRepository.findById(id)
                .orElseThrow(() -> new WindTurbineNotFoundException(id));

        if (turbine.getMaintenance() == MaintenanceStatus.UNDER_MAINTENANCE) {
            throw new InvalidOperationException(
                    "Cannot record power output. Turbine " + id + " is under maintenance.");
        }

        if (request.getPowerOutputValue() > turbine.getCapacity()) {
            throw new InvalidOperationException(
                    "Power output value cannot exceed turbine capacity of " + turbine.getCapacity());
        }

        turbine.setCurrentOutput(request.getPowerOutputValue());
        WindTurbine updated = windTurbineRepository.save(turbine);
        return windTurbineMapper.toResponse(updated);
    }

    @Override
    public Double getTotalCurrentOutput() {
        Double total = windTurbineRepository.sumCurrentOutput();
        return total == null ? 0.0 : total;
    }

    @Override
    public List<WindTurbineResponse> getZeroOutputFaults() {
        return windTurbineRepository.findZeroOutputOperationalTurbines().stream()
                .map(windTurbineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WindTurbineResponse> getTurbinesUnderMaintenance() {
        return windTurbineRepository.findByMaintenance(MaintenanceStatus.UNDER_MAINTENANCE).stream()
                .map(windTurbineMapper::toResponse)
                .collect(Collectors.toList());
    }
}