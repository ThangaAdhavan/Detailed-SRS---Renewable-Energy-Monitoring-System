package com.renewable.solar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.entity.EquipmentStatus;
import com.renewable.solar.entity.MaintenanceStatus;
import com.renewable.solar.service.SolarPanelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolarPanelController.class)
class SolarPanelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolarPanelService solarPanelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerPanel_shouldReturnCreated() throws Exception {
        SolarPanelCreateRequest request = new SolarPanelCreateRequest(
                "Panel-B1", "Rooftop-South", 400.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);

        SolarPanelResponse response = SolarPanelResponse.builder()
                .id(1L)
                .deviceName("Panel-B1")
                .location("Rooftop-South")
                .capacity(400.0)
                .currentGeneration(0.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();

        when(solarPanelService.registerPanel(any())).thenReturn(response);

        mockMvc.perform(post("/api/solar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceName").value("Panel-B1"));
    }

    @Test
    void registerPanel_withBlankDeviceName_shouldReturnBadRequest() throws Exception {
        SolarPanelCreateRequest request = new SolarPanelCreateRequest(
                "", "Rooftop-South", 400.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);

        mockMvc.perform(post("/api/solar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPanel_shouldReturnOk() throws Exception {
        SolarPanelResponse response = SolarPanelResponse.builder()
                .id(1L)
                .deviceName("Panel-B1")
                .location("Rooftop-South")
                .capacity(400.0)
                .currentGeneration(120.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();

        when(solarPanelService.getPanel(1L)).thenReturn(response);

        mockMvc.perform(get("/api/solar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currentGeneration").value(120.0));
    }
}
