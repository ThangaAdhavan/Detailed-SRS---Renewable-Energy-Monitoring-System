package com.renewable.distribution.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRequest {

    @NotNull(message = "Demand value is required")
    @Positive(message = "Demand must be positive")
    private Double demandKwh;
}
