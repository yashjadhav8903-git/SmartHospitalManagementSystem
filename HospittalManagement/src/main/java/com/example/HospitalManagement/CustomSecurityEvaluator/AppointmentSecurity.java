package com.example.HospitalManagement.CustomSecurityEvaluator;

import com.example.HospitalManagement.Repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("appointmentSecurity")
@RequiredArgsConstructor
public class AppointmentSecurity {

    private final AppointmentRepository appointmentRepository;

    // Direct DB ownership check via Username (SecurityContextHolder se String username aayega)
    public boolean isAppointmentOwner(Integer appointmentId,String username) {
        return appointmentRepository.findById(appointmentId)
                .map(app -> app.getPatient() != null && app.getPatient().getUserEntity() != null
                && username.equals(app.getPatient().getUserEntity().getUsername()))
                .orElse(false);
    }

    public boolean isDoctorOwner(Integer doctorId,String username) {
        return appointmentRepository.existsByDoctorIdAndDoctorUserEntityUsername(doctorId, username);
    }

    public boolean isPatientOwner(Integer patientId,String username) {
        return appointmentRepository.existsByPatientIdAndPatientUserEntityUsername(patientId, username);
    }

    public boolean isAppointmentOwnerForPatient (Integer appointmentId, String username) {
        return appointmentRepository.existsByPatientIdAndPatientUserEntityUsername(appointmentId, username);
    }
}
