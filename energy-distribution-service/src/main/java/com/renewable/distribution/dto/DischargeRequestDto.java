package com.renewable.distribution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Outbound payload sent to battery-service's /discharge endpoint. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DischargeRequestDto {
    private Double energyAmountKwh;
}
