package com.example.HospitalManagement.Projection.ForAppointmens;

import com.example.HospitalManagement.Enums.AppointmentStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentProjection {

    Integer getAppointmentId();
    LocalDateTime getAppointmentTime();
    String getAppointmentReason();
    String getPatientName();
    String getDoctorName();
    String getAppointmentStatus();
}
