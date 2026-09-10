package com.example.HospitalManagement.ExceptionHandling;

public class InsuranceExpiredException extends RuntimeException{
    public InsuranceExpiredException(String message){
        super(message);
    }
}
