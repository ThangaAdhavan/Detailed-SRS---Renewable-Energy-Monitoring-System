package com.renewable.wind.controller;

import com.renewable.wind.dto.PowerOutputRecordRequest;
import com.renewable.wind.dto.WindTurbineCreateRequest;
import com.renewable.wind.dto.WindTurbineResponse;
import com.renewable.wind.dto.WindTurbineUpdateRequest;
import com.renewable.wind.service.WindTurbineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wind")
@Tag(name = "Wind Turbine Management", description = "Endpoints for managing wind turbines and power output data")
public class WindTurbineController {

    private final WindTurbineService windTurbineService;

    public WindTurbineController(WindTurbineService windTurbineService) {
        this.windTurbineService = windTurbineService;
    }

    @PostMapping
    @Operation(summary = "Register a new wind turbine")
    public ResponseEntity<WindTurbineResponse> registerTurbine(@Valid @RequestBody WindTurbineCreateRequest request) {
        WindTurbineResponse response = windTurbineService.registerTurbine(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing wind turbine")
    public ResponseEntity<WindTurbineResponse> updateTurbine(@PathVariable Long id,
                                                           @Valid @RequestBody WindTurbineUpdateRequest request) {
        WindTurbineResponse response = windTurbineService.updateTurbine(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a wind turbine")
    public ResponseEntity<Void> deleteTurbine(@PathVariable Long id) {
        windTurbineService.deleteTurbine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a wind turbine by id")
    public ResponseEntity<WindTurbineResponse> getTurbine(@PathVariable Long id) {
        return ResponseEntity.ok(windTurbineService.getTurbine(id));
    }

    @GetMapping
    @Operation(summary = "Get all wind turbines")
    public ResponseEntity<List<WindTurbineResponse>> getAllTurbines() {
        return ResponseEntity.ok(windTurbineService.getAllTurbines());
    }

    @PostMapping("/{id}/power-output")
    @Operation(summary = "Record hourly power output for a wind turbine")
    public ResponseEntity<WindTurbineResponse> recordPowerOutput(@PathVariable Long id,
                                                                @Valid @RequestBody PowerOutputRecordRequest request) {
        return ResponseEntity.ok(windTurbineService.recordPowerOutput(id, request));
    }

    @GetMapping("/power-output/total")
    @Operation(summary = "Get total current power output across all operational turbines")
    public ResponseEntity<Map<String, Double>> getTotalOutput() {
        return ResponseEntity.ok(Map.of("totalOutput", windTurbineService.getTotalCurrentOutput()));
    }

    @GetMapping("/faults/zero-output")
    @Operation(summary = "Get operational turbines currently producing zero power output")
    public ResponseEntity<List<WindTurbineResponse>> getZeroOutputFaults() {
        return ResponseEntity.ok(windTurbineService.getZeroOutputFaults());
    }

    @GetMapping("/maintenance")
    @Operation(summary = "Get all wind turbines currently under maintenance")
    public ResponseEntity<List<WindTurbineResponse>> getTurbinesUnderMaintenance() {
        return ResponseEntity.ok(windTurbineService.getTurbinesUnderMaintenance());
    }
}