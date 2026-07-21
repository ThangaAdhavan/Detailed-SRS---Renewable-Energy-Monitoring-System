package com.renewable.wind.dto;

import com.renewable.wind.validation.ValidHourOfDay;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerOutputRecordRequest {

    @NotNull(message = "Power output value is required")
    @PositiveOrZero(message = "Power output must be zero or positive")
    private Double powerOutputValue;

    @NotNull(message = "Hour of day is required (0-23)")
    @ValidHourOfDay
    private Integer hourOfDay;
}
