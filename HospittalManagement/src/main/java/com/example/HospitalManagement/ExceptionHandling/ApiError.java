package com.example.HospitalManagement.ExceptionHandling;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {

    private LocalDateTime timeStamp;
    private String error;
    private HttpStatus statusCode;
    private String path;


    public ApiError(){
        this.timeStamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus statusCode,String error,String path){
        this();
        this.error = error;
        this.statusCode = statusCode;
        this.path = path;

    }
}
