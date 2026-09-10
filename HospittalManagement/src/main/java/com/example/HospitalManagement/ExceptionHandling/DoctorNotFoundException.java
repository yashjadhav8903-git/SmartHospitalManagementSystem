package com.example.HospitalManagement.ExceptionHandling;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(String message){
        super(message);
    }
}
