package com.renewable.solar.exception;

public class SolarPanelNotFoundException extends RuntimeException {

    public SolarPanelNotFoundException(String message) {
        super(message);
    }

    public SolarPanelNotFoundException(Long id) {
        super("Solar panel not found with id: " + id);
    }
}
