package com.example.HospitalManagement.ExceptionHandling;

public class PatientNotFoundException extends RuntimeException{

    public PatientNotFoundException(String message){
        super(message);
    }
}
