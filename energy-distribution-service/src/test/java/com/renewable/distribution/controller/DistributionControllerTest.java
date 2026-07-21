package com.renewable.distribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renewable.distribution.dto.DistributionRequest;
import com.renewable.distribution.dto.DistributionResponse;
import com.renewable.distribution.service.DistributionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DistributionController.class)
class DistributionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DistributionService distributionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processDistribution_shouldReturnCreated() throws Exception {
        DistributionRequest request = new DistributionRequest(150.0);

        DistributionResponse response = DistributionResponse.builder()
                .id(1L)
                .requestedDemand(150.0)
                .renewablePower(150.0)
                .batteryPower(0.0)
                .distributedPower(150.0)
                .remainingPower(0.0)
                .distributionDate(LocalDateTime.now())
                .build();

        when(distributionService.processDistribution(any())).thenReturn(response);

        mockMvc.perform(post("/api/distribution/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.distributedPower").value(150.0));
    }

    @Test
    void processDistribution_withMissingDemand_shouldReturnBadRequest() throws Exception {
        DistributionRequest request = new DistributionRequest(null);

        mockMvc.perform(post("/api/distribution/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
