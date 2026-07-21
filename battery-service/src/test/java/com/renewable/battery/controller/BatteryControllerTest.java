package com.renewable.battery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewable.battery.dto.BatteryCreateRequest;
import com.renewable.battery.dto.BatteryResponse;
import com.renewable.battery.entity.BatteryStatus;
import com.renewable.battery.service.BatteryService;
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

@WebMvcTest(BatteryController.class)
class BatteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BatteryService batteryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBattery_shouldReturnCreated() throws Exception {
        BatteryCreateRequest request = new BatteryCreateRequest("Battery-1", "Storage-Hall-A", 100.0, BatteryStatus.ACTIVE);

        BatteryResponse response = BatteryResponse.builder()
                .id(1L)
                .deviceName("Battery-1")
                .location("Storage-Hall-A")
                .capacity(100.0)
                .chargePercentage(0.0)
                .availableCapacity(100.0)
                .remainingCapacity(0.0)
                .status(BatteryStatus.ACTIVE)
                .lowBatteryAlert(true)
                .build();

        when(batteryService.createBattery(any())).thenReturn(response);

        mockMvc.perform(post("/api/battery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceName").value("Battery-1"));
    }

    @Test
    void createBattery_withMissingLocation_shouldReturnBadRequest() throws Exception {
        BatteryCreateRequest request = new BatteryCreateRequest("Battery-1", "", 100.0, BatteryStatus.ACTIVE);

        mockMvc.perform(post("/api/battery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
