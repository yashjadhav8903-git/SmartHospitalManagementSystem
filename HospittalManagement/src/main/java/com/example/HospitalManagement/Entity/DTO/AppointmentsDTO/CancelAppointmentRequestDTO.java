package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequestDTO {

    private Integer appointmentId;
    private Integer patientId;
    private String cancelReason;
}
