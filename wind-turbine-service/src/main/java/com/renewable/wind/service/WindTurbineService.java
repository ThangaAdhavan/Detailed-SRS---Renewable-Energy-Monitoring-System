package com.renewable.wind.service;

import com.renewable.wind.dto.PowerOutputRecordRequest;
import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.dto.WindTurbineUpdateRequest;

import java.util.List;

public interface WindTurbineService {

    WindTurbineResponse registerTurbine(WindTurbineCreateRequest request);

    WindTurbineResponse updateTurbine(Long id, WindTurbineUpdateRequest request);

    void deleteTurbine(Long id);

    WindTurbineResponse getTurbine(Long id);

    List<WindTurbineResponse> getAllTurbines();

    WindTurbineResponse recordPowerOutput(Long id, PowerOutputRecordRequest request);

    Double getTotalCurrentOutput();

    List<WindTurbineResponse> getZeroOutputFaults();

    List<WindTurbineResponse> getTurbinesUnderMaintenance();
}
