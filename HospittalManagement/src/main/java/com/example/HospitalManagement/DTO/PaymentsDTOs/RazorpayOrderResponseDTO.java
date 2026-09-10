package com.example.HospitalManagement.DTO.PaymentsDTOs;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RazorpayOrderResponseDTO {

    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private Integer appointmentId;
    private String razorpayKeyId;
}
