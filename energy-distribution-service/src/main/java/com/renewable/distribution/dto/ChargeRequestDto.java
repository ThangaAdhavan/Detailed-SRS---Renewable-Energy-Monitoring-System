package com.renewable.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Outbound payload sent to battery-service's /charge endpoint. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequestDto {
    private Double energyAmountKwh;
}
