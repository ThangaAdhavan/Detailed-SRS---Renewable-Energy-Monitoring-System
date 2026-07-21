package com.renewable.distribution.repository;

import com.renewable.distribution.entity.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DistributionRepository extends JpaRepository<Distribution, Long> {

    @Query("SELECT d FROM Distribution d WHERE d.distributionDate BETWEEN :start AND :end ORDER BY d.distributionDate DESC")
    List<Distribution> findByDistributionDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Distribution> findAllByOrderByDistributionDateDesc();
}
