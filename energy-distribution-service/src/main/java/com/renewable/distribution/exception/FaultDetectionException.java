package com.renewable.distribution.exception;

public class FaultDetectionException extends RuntimeException {

    public FaultDetectionException(String message) {
        super(message);
    }

    public FaultDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
