package com.renewable.wind.repository;

import com.renewable.wind.entity.EquipmentStatus;
import com.renewable.wind.entity.MaintenanceStatus;
import com.renewable.wind.entity.WindTurbine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WindTurbineRepository extends JpaRepository<WindTurbine, Long> {

    List<WindTurbine> findByStatus(EquipmentStatus status);

    List<WindTurbine> findByMaintenance(MaintenanceStatus maintenance);

    List<WindTurbine> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT s FROM WindTurbine s WHERE s.currentOutput = 0 AND s.maintenance = 'OPERATIONAL'")
    List<WindTurbine> findZeroOutputOperationalTurbines();

    @Query("SELECT COALESCE(SUM(s.currentOutput), 0) FROM WindTurbine s WHERE s.maintenance = 'OPERATIONAL'")
    Double sumCurrentOutput();
}
