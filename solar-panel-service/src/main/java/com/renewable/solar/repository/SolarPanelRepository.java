package com.renewable.solar.repository;

import com.renewable.solar.entity.EquipmentStatus;
import com.renewable.solar.entity.MaintenanceStatus;
import com.renewable.solar.entity.SolarPanel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolarPanelRepository extends JpaRepository<SolarPanel, Long> {

    List<SolarPanel> findByStatus(EquipmentStatus status);

    List<SolarPanel> findByMaintenance(MaintenanceStatus maintenance);

    List<SolarPanel> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT s FROM SolarPanel s WHERE s.currentGeneration = 0 AND s.maintenance = 'OPERATIONAL'")
    List<SolarPanel> findZeroGenerationOperationalPanels();

    @Query("SELECT COALESCE(SUM(s.currentGeneration), 0) FROM SolarPanel s WHERE s.maintenance = 'OPERATIONAL'")
    Double sumCurrentGeneration();
}
