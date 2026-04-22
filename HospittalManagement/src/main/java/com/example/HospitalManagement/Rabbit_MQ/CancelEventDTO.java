package com.example.HospitalManagement.Rabbit_MQ;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CancelEventDTO {
    private Integer id;
    private String email;
    private String patientName;
    private Integer slot;
    private String docterName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;
    private LocalDate date;
    private String reason;
}
