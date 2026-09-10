package com.example.HospitalManagement.DTO.PaymentsDTOs;

import lombok.Data;

@Data
public class VerifyPaymentRequestDTO {

    private Integer appointmentId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
