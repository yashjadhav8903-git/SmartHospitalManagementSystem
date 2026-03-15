package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;

import com.example.HospitalManagement.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelAppointmentResponseDTO {

    private Integer appointmentId;
    private String doctorName;
    private String patientName;
    private AppointmentStatus appointmentStatus;
    private String cancelReason;
}
