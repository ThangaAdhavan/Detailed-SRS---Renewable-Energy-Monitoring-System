package com.renewable.wind.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.entity.EquipmentStatus;
import com.renewable.wind.entity.MaintenanceStatus;
import com.renewable.wind.service.WindTurbineService;
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

@WebMvcTest(WindTurbineController.class)
class WindTurbineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WindTurbineService windTurbineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerTurbine_shouldReturnCreated() throws Exception {
        WindTurbineCreateRequest request = new WindTurbineCreateRequest(
                "Turbine-B1", "Rooftop-South", 400.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);

        WindTurbineResponse response = WindTurbineResponse.builder()
                .id(1L)
                .deviceName("Turbine-B1")
                .location("Rooftop-South")
                .capacity(400.0)
                .currentOutput(0.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();

        when(windTurbineService.registerTurbine(any())).thenReturn(response);

        mockMvc.perform(post("/api/wind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceName").value("Turbine-B1"));
    }

    @Test
    void registerTurbine_withBlankDeviceName_shouldReturnBadRequest() throws Exception {
        WindTurbineCreateRequest request = new WindTurbineCreateRequest(
                "", "Rooftop-South", 400.0, EquipmentStatus.ACTIVE, MaintenanceStatus.OPERATIONAL);

        mockMvc.perform(post("/api/wind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTurbine_shouldReturnOk() throws Exception {
        WindTurbineResponse response = WindTurbineResponse.builder()
                .id(1L)
                .deviceName("Turbine-B1")
                .location("Rooftop-South")
                .capacity(400.0)
                .currentOutput(120.0)
                .status(EquipmentStatus.ACTIVE)
                .maintenance(MaintenanceStatus.OPERATIONAL)
                .build();

        when(windTurbineService.getTurbine(1L)).thenReturn(response);

        mockMvc.perform(get("/api/wind/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currentOutput").value(120.0));
    }
}
