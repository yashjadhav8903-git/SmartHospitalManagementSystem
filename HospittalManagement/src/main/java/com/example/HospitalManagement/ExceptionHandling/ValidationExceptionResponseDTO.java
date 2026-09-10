package com.example.HospitalManagement.ExceptionHandling;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationExceptionResponseDTO {

    private LocalDateTime localDateTime;
    private Integer statusCode;
    private String error;
    private String message;
    private String path;
    private Map<String,String> fieldErrors;

    public ValidationExceptionResponseDTO(LocalDateTime localDateTime, Integer statusCode, String error, String message, String path, Map<String, String> fieldErrors) {
        this.localDateTime = localDateTime;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
