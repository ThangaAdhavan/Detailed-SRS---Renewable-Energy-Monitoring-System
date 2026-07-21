package com.renewable.wind.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HourOfDayValidator implements ConstraintValidator<ValidHourOfDay, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value >= 0 && value <= 23;
    }
}
