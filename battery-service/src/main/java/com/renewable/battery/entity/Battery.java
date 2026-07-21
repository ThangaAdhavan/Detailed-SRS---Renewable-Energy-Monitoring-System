package com.renewable.battery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "batteries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "location", nullable = false)
    private String location;

    /** Total storage capacity in kWh. */
    @Column(name = "capacity", nullable = false)
    private Double capacity;

    /** Current charge level expressed as a percentage (0-100). */
    @Column(name = "charge_percentage", nullable = false)
    private Double chargePercentage;

    /** Remaining capacity that can still be charged, in kWh. */
    @Column(name = "available_capacity")
    private Double availableCapacity;

    /** Energy currently stored, in kWh (derived from chargePercentage * capacity). */
    @Column(name = "remaining_capacity")
    private Double remainingCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BatteryStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.chargePercentage == null) {
            this.chargePercentage = 0.0;
        }
        recalculateCapacities();
    }

    public void recalculateCapacities() {
        this.remainingCapacity = round2((this.chargePercentage / 100.0) * this.capacity);
        this.availableCapacity = round2(this.capacity - this.remainingCapacity);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
