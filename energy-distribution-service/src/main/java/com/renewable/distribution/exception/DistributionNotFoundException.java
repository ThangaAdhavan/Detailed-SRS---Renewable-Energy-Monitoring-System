package com.renewable.distribution.exception;

public class DistributionNotFoundException extends RuntimeException {

    public DistributionNotFoundException(Long id) {
        super("Distribution record not found with id: " + id);
    }
}
