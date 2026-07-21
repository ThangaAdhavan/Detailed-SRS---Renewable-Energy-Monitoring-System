package com.renewable.distribution.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "distributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Distribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Total renewable power available from solar + wind, in kWh. */
    @Column(name = "renewable_power", nullable = false)
    private Double renewablePower;

    /** Power drawn from or sent to battery storage (positive = discharged to meet demand, negative = charged with excess). */
    @Column(name = "battery_power", nullable = false)
    private Double batteryPower;

    /** Total power actually distributed to meet demand. */
    @Column(name = "distributed_power", nullable = false)
    private Double distributedPower;

    /** Demand that could not be met by renewable + battery combined. */
    @Column(name = "remaining_power", nullable = false)
    private Double remainingPower;

    @Column(name = "requested_demand", nullable = false)
    private Double requestedDemand;

    @Column(name = "distribution_date", nullable = false)
    private LocalDateTime distributionDate;

    @PrePersist
    protected void onCreate() {
        this.distributionDate = LocalDateTime.now();
    }
}
