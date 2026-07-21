package com.renewable.solar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "solar_panels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "capacity", nullable = false)
    private Double capacity;

    @Column(name = "current_generation")
    private Double currentGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EquipmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance", nullable = false)
    private MaintenanceStatus maintenance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currentGeneration == null) {
            this.currentGeneration = 0.0;
        }
    }
}
