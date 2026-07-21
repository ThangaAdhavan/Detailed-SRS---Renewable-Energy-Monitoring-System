package com.renewable.solar.util;

import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.dto.SolarPanelUpdateRequest;
import com.renewable.solar.entity.SolarPanel;
import org.springframework.stereotype.Component;

@Component
public class SolarPanelMapper {

    public SolarPanel toEntity(SolarPanelCreateRequest request) {
        return SolarPanel.builder()
                .deviceName(request.getDeviceName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .currentGeneration(0.0)
                .status(request.getStatus())
                .maintenance(request.getMaintenance())
                .build();
    }

    public void updateEntityFromRequest(SolarPanel panel, SolarPanelUpdateRequest request) {
        panel.setDeviceName(request.getDeviceName());
        panel.setLocation(request.getLocation());
        panel.setCapacity(request.getCapacity());
        panel.setStatus(request.getStatus());
        panel.setMaintenance(request.getMaintenance());
    }

    public SolarPanelResponse toResponse(SolarPanel panel) {
        return SolarPanelResponse.builder()
                .id(panel.getId())
                .deviceName(panel.getDeviceName())
                .location(panel.getLocation())
                .capacity(panel.getCapacity())
                .currentGeneration(panel.getCurrentGeneration())
                .status(panel.getStatus())
                .maintenance(panel.getMaintenance())
                .createdAt(panel.getCreatedAt())
                .build();
    }
}
