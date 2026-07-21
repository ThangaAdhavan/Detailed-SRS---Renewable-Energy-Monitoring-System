package com.renewable.solar.controller;

import com.renewable.solar.dto.GenerationRecordRequest;
import com.renewable.solar.dto.SolarPanelCreateRequest;
import com.renewable.solar.dto.SolarPanelResponse;
import com.renewable.solar.dto.SolarPanelUpdateRequest;
import com.renewable.solar.service.SolarPanelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solar")
@Tag(name = "Solar Panel Management", description = "Endpoints for managing solar panels and generation data")
public class SolarPanelController {

    private final SolarPanelService solarPanelService;

    public SolarPanelController(SolarPanelService solarPanelService) {
        this.solarPanelService = solarPanelService;
    }

    @PostMapping
    @Operation(summary = "Register a new solar panel")
    public ResponseEntity<SolarPanelResponse> registerPanel(@Valid @RequestBody SolarPanelCreateRequest request) {
        SolarPanelResponse response = solarPanelService.registerPanel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing solar panel")
    public ResponseEntity<SolarPanelResponse> updatePanel(@PathVariable Long id,
                                                           @Valid @RequestBody SolarPanelUpdateRequest request) {
        SolarPanelResponse response = solarPanelService.updatePanel(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a solar panel")
    public ResponseEntity<Void> deletePanel(@PathVariable Long id) {
        solarPanelService.deletePanel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a solar panel by id")
    public ResponseEntity<SolarPanelResponse> getPanel(@PathVariable Long id) {
        return ResponseEntity.ok(solarPanelService.getPanel(id));
    }

    @GetMapping
    @Operation(summary = "Get all solar panels")
    public ResponseEntity<List<SolarPanelResponse>> getAllPanels() {
        return ResponseEntity.ok(solarPanelService.getAllPanels());
    }

    @PostMapping("/{id}/generation")
    @Operation(summary = "Record hourly generation for a solar panel")
    public ResponseEntity<SolarPanelResponse> recordGeneration(@PathVariable Long id,
                                                                @Valid @RequestBody GenerationRecordRequest request) {
        return ResponseEntity.ok(solarPanelService.recordGeneration(id, request));
    }

    @GetMapping("/generation/total")
    @Operation(summary = "Get total current generation across all operational panels")
    public ResponseEntity<Map<String, Double>> getTotalGeneration() {
        return ResponseEntity.ok(Map.of("totalGeneration", solarPanelService.getTotalCurrentGeneration()));
    }

    @GetMapping("/faults/zero-generation")
    @Operation(summary = "Get operational panels currently producing zero generation")
    public ResponseEntity<List<SolarPanelResponse>> getZeroGenerationFaults() {
        return ResponseEntity.ok(solarPanelService.getZeroGenerationFaults());
    }

    @GetMapping("/maintenance")
    @Operation(summary = "Get all solar panels currently under maintenance")
    public ResponseEntity<List<SolarPanelResponse>> getPanelsUnderMaintenance() {
        return ResponseEntity.ok(solarPanelService.getPanelsUnderMaintenance());
    }
}