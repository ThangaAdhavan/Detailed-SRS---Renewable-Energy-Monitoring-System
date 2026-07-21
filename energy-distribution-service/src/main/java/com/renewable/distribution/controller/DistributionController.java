package com.renewable.distribution.controller;

import com.renewable.distribution.dto.*;
import com.renewable.distribution.service.DistributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/distribution")
@Tag(name = "Energy Distribution & Fault Detection", description = "Endpoints for distributing renewable energy, detecting faults, and generating reports")
public class DistributionController {

    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process a distribution request: aggregate renewable generation, use battery storage, and meet demand")
    public ResponseEntity<DistributionResponse> processDistribution(@Valid @RequestBody DistributionRequest request) {
        return new ResponseEntity<>(distributionService.processDistribution(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a distribution record by id")
    public ResponseEntity<DistributionResponse> getDistribution(@PathVariable Long id) {
        return ResponseEntity.ok(distributionService.getDistribution(id));
    }

    @GetMapping
    @Operation(summary = "Get all distribution records (distribution summary history)")
    public ResponseEntity<List<DistributionResponse>> getAllDistributions() {
        return ResponseEntity.ok(distributionService.getAllDistributions());
    }

    @PostMapping("/faults/detect")
    @Operation(summary = "Run fault detection across solar, wind, and battery services and persist any faults found")
    public ResponseEntity<List<FaultResponse>> detectFaults() {
        return ResponseEntity.ok(distributionService.detectFaults());
    }

    @GetMapping("/faults")
    @Operation(summary = "Get the full fault history")
    public ResponseEntity<List<FaultResponse>> getFaultHistory() {
        return ResponseEntity.ok(distributionService.getFaultHistory());
    }

    @GetMapping("/report/daily")
    @Operation(summary = "Generate the daily distribution and fault report for a given date (defaults to today)")
    public ResponseEntity<DailyReportResponse> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate reportDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(distributionService.getDailyReport(reportDate));
    }
}
