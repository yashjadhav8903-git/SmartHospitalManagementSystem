package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;

import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentResponseDTO {

    private Integer appointmentId;
    private LocalDateTime appointmentTime;
    private Integer slot;
    private String reason;
    private String patientName;
    private String doctorName;
    private AppointmentStatus appointmentStatus;

}
