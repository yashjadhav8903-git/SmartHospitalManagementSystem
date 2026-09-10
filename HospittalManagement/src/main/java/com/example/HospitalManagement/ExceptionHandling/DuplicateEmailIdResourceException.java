package com.example.HospitalManagement.ExceptionHandling;

public class DuplicateEmailIdResourceException extends RuntimeException{

    public DuplicateEmailIdResourceException(String message){
        super(message);
    }
}
