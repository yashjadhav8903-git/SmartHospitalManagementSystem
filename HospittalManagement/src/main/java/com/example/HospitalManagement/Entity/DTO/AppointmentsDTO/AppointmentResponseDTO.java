package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO implements Serializable {

    private Integer appointmentId;
    private LocalDateTime appointmentTime;
    private String reason;
    private String patientName;
    private String doctorName;

}
