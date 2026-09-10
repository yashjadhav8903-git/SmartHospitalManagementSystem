package com.example.HospitalManagement.ExceptionHandling;

public class InsuranceNotFoundException extends RuntimeException {

    public InsuranceNotFoundException(String message) {
        super(message);
    }
}
