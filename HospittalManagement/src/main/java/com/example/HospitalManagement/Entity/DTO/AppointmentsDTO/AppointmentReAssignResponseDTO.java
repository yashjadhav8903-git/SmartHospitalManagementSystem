package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;

import com.example.HospitalManagement.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReAssignResponseDTO {

    private Integer appointmentId;
    private String patientName;
    private LocalDateTime appointmentTime;

    private String doctorName;
    private Integer doctorId;

    private AppointmentStatus status;
}
