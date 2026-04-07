package com.example.HospitalManagement.Entity.DTO.AppointmentsDTO;

import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentRequestDTO {

    @NotBlank(message = "Appointment-Time")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Ya jo bhi format tum bhej rahe ho
    private LocalDateTime appointmentTime;
    private Integer slot;
    @NotBlank(message = "Appointment Reason")
    private String appointmentReason;
    private Integer patientId;
    private Integer doctorId;
}
