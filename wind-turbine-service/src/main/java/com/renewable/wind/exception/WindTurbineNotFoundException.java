package com.renewable.wind.exception;

public class WindTurbineNotFoundException extends RuntimeException {

    public WindTurbineNotFoundException(String message) {
        super(message);
    }

    public WindTurbineNotFoundException(Long id) {
        super("Wind turbine not found with id: " + id);
    }
}
