package com.renewable.battery.exception;

public class BatteryNotFoundException extends RuntimeException {

    public BatteryNotFoundException(String message) {
        super(message);
    }

    public BatteryNotFoundException(Long id) {
        super("Battery not found with id: " + id);
    }
}
