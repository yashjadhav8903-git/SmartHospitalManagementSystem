package com.example.HospitalManagement.ExceptionHandling;

import com.example.HospitalManagement.Rabbit_MQ.RabbitMQConfig;

public class NoActiveInsurancePolicyFoundException extends RuntimeException {

    public NoActiveInsurancePolicyFoundException(String message) {
        super(message);
    }
}
