package com.renewable.battery.controller;

import com.renewable.battery.dto.*;
import com.renewable.battery.service.BatteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/battery")
@Tag(name = "Battery Management", description = "Endpoints for managing battery storage units, charging, and discharging")
public class BatteryController {

    private final BatteryService batteryService;

    public BatteryController(BatteryService batteryService) {
        this.batteryService = batteryService;
    }

    @PostMapping
    @Operation(summary = "Create a new battery storage unit")
    public ResponseEntity<BatteryResponse> createBattery(@Valid @RequestBody BatteryCreateRequest request) {
        return new ResponseEntity<>(batteryService.createBattery(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing battery")
    public ResponseEntity<BatteryResponse> updateBattery(@PathVariable Long id,
                                                          @Valid @RequestBody BatteryUpdateRequest request) {
        return ResponseEntity.ok(batteryService.updateBattery(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a battery")
    public ResponseEntity<Void> deleteBattery(@PathVariable Long id) {
        batteryService.deleteBattery(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get battery status by id")
    public ResponseEntity<BatteryResponse> getBattery(@PathVariable Long id) {
        return ResponseEntity.ok(batteryService.getBattery(id));
    }

    @GetMapping
    @Operation(summary = "Get all batteries")
    public ResponseEntity<List<BatteryResponse>> getAllBatteries() {
        return ResponseEntity.ok(batteryService.getAllBatteries());
    }

    @PostMapping("/{id}/charge")
    @Operation(summary = "Charge a battery with a given amount of energy (kWh)")
    public ResponseEntity<BatteryResponse> chargeBattery(@PathVariable Long id,
                                                          @Valid @RequestBody ChargeRequest request) {
        return ResponseEntity.ok(batteryService.chargeBattery(id, request));
    }

    @PostMapping("/{id}/discharge")
    @Operation(summary = "Discharge a battery by a given amount of energy (kWh)")
    public ResponseEntity<BatteryResponse> dischargeBattery(@PathVariable Long id,
                                                             @Valid @RequestBody DischargeRequest request) {
        return ResponseEntity.ok(batteryService.dischargeBattery(id, request));
    }

    @GetMapping("/{id}/available-capacity")
    @Operation(summary = "Get available (chargeable) capacity for a battery")
    public ResponseEntity<Map<String, Double>> getAvailableCapacity(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("availableCapacity", batteryService.getAvailableCapacity(id)));
    }

    @GetMapping("/{id}/remaining-capacity")
    @Operation(summary = "Get remaining stored capacity for a battery")
    public ResponseEntity<Map<String, Double>> getRemainingCapacity(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("remainingCapacity", batteryService.getRemainingCapacity(id)));
    }

    @GetMapping("/capacity/total-available")
    @Operation(summary = "Get total available capacity across all batteries")
    public ResponseEntity<Map<String, Double>> getTotalAvailableCapacity() {
        return ResponseEntity.ok(Map.of("totalAvailableCapacity", batteryService.getTotalAvailableCapacity()));
    }

    @GetMapping("/capacity/total-remaining")
    @Operation(summary = "Get total remaining (stored) capacity across all batteries")
    public ResponseEntity<Map<String, Double>> getTotalRemainingCapacity() {
        return ResponseEntity.ok(Map.of("totalRemainingCapacity", batteryService.getTotalRemainingCapacity()));
    }

    @GetMapping("/alerts/low-battery")
    @Operation(summary = "Get low battery alerts for all batteries below the threshold")
    public ResponseEntity<List<LowBatteryAlertResponse>> getLowBatteryAlerts() {
        return ResponseEntity.ok(batteryService.getLowBatteryAlerts());
    }
}
