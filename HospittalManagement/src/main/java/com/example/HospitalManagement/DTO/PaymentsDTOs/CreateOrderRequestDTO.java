package com.example.HospitalManagement.DTO.PaymentsDTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequestDTO {

    private Integer appointmentId;
    private BigDecimal amount;
}
