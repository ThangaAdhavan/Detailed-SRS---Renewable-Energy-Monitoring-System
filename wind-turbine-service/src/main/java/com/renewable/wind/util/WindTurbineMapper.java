package com.renewable.wind.util;

import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.dto.WindTurbineUpdateRequest;
import com.renewable.wind.entity.WindTurbine;
import org.springframework.stereotype.Component;

@Component
public class WindTurbineMapper {

    public WindTurbine toEntity(WindTurbineCreateRequest request) {
        return WindTurbine.builder()
                .deviceName(request.getDeviceName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .currentOutput(0.0)
                .status(request.getStatus())
                .maintenance(request.getMaintenance())
                .build();
    }

    public void updateEntityFromRequest(WindTurbine turbine, WindTurbineUpdateRequest request) {
        turbine.setDeviceName(request.getDeviceName());
        turbine.setLocation(request.getLocation());
        turbine.setCapacity(request.getCapacity());
        turbine.setStatus(request.getStatus());
        turbine.setMaintenance(request.getMaintenance());
    }

    public WindTurbineResponse toResponse(WindTurbine turbine) {
        return WindTurbineResponse.builder()
                .id(turbine.getId())
                .deviceName(turbine.getDeviceName())
                .location(turbine.getLocation())
                .capacity(turbine.getCapacity())
                .currentOutput(turbine.getCurrentOutput())
                .status(turbine.getStatus())
                .maintenance(turbine.getMaintenance())
                .createdAt(turbine.getCreatedAt())
                .build();
    }
}
