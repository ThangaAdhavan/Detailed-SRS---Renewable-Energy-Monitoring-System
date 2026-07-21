package com.renewable.battery.repository;

import com.renewable.battery.entity.Battery;
import com.renewable.battery.entity.BatteryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BatteryRepository extends JpaRepository<Battery, Long> {

    List<Battery> findByStatus(BatteryStatus status);

    @Query("SELECT b FROM Battery b WHERE b.chargePercentage < :threshold")
    List<Battery> findBatteriesBelowThreshold(double threshold);

    @Query("SELECT COALESCE(SUM(b.remainingCapacity), 0) FROM Battery b")
    Double sumRemainingCapacity();

    @Query("SELECT COALESCE(SUM(b.availableCapacity), 0) FROM Battery b")
    Double sumAvailableCapacity();
}
