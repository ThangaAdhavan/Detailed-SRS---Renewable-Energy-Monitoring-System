package com.renewable.solar.dto;

import com.renewable.solar.validation.ValidHourOfDay;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerationRecordRequest {

    @NotNull(message = "Generation value is required")
    @PositiveOrZero(message = "Generation must be zero or positive")
    private Double generationValue;

    @NotNull(message = "Hour of day is required (0-23)")
    @ValidHourOfDay
    private Integer hourOfDay;
}
