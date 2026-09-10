package com.example.HospitalManagement.Rabbit_MQ.EventDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEventDTO {

    private String actor;
    private String action;
    private String resource;
    private String ipAddress;
    private String status;
    private String details;
    private LocalDateTime timestamp;

}
