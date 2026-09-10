package com.example.HospitalManagement.ExceptionHandling;

import jakarta.persistence.EntityNotFoundException;

public class AppointmentNotFoundException extends EntityNotFoundException {

    public AppointmentNotFoundException(String message){
        super(message);
    }
}
