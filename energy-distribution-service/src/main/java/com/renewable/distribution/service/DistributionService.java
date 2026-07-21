package com.renewable.distribution.service;

import com.renewable.distribution.dto.DailyReportResponse;
import com.renewable.distribution.dto.DistributionRequest;
import com.renewable.distribution.dto.DistributionResponse;
import com.renewable.distribution.dto.FaultResponse;

import java.time.LocalDate;
import java.util.List;

public interface DistributionService {

    DistributionResponse processDistribution(DistributionRequest request);

    DistributionResponse getDistribution(Long id);

    List<DistributionResponse> getAllDistributions();

    List<FaultResponse> detectFaults();

    List<FaultResponse> getFaultHistory();

    DailyReportResponse getDailyReport(LocalDate date);
}
