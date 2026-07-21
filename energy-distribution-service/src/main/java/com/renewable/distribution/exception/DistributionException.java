package com.renewable.distribution.exception;

public class DistributionException extends RuntimeException {

    public DistributionException(String message) {
        super(message);
    }

    public DistributionException(String message, Throwable cause) {
        super(message, cause);
    }
}
