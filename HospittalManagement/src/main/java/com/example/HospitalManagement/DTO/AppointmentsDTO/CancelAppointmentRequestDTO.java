package com.example.HospitalManagement.DTO.AppointmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentRequestDTO {

    private Integer appointmentId;
    private String cancelReason;
}
