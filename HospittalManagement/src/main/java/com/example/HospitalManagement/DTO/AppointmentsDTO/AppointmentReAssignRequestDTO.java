package com.example.HospitalManagement.DTO.AppointmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReAssignRequestDTO {

    private Integer appointmentId;
    private Integer newDoctorId;
}
