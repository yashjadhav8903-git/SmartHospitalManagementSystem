package com.example.HospitalManagement.DTO.AppointmentsDTO;

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


    private Integer slot;
    @NotBlank(message = "Appointment Reason")
    private String appointmentReason;
    private Integer doctorId;
}
