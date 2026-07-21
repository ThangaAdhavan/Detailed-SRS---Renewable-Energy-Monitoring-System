package com.renewable.distribution.repository;

import com.renewable.distribution.entity.DeviceType;
import com.renewable.distribution.entity.Fault;
import com.renewable.distribution.entity.FaultType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FaultRepository extends JpaRepository<Fault, Long> {

    List<Fault> findByDeviceType(DeviceType deviceType);

    List<Fault> findByFaultType(FaultType faultType);

    @Query("SELECT f FROM Fault f WHERE f.createdAt BETWEEN :start AND :end ORDER BY f.createdAt DESC")
    List<Fault> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Fault> findAllByOrderByCreatedAtDesc();
}
