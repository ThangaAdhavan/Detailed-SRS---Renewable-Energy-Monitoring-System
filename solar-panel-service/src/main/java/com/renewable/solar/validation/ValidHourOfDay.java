package com.renewable.solar.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HourOfDayValidator.class)
public @interface ValidHourOfDay {
    String message() default "Hour of day must be between 0 and 23";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
