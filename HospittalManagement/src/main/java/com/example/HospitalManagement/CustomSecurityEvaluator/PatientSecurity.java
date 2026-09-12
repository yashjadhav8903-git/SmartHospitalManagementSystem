package com.example.HospitalManagement.CustomSecurityEvaluator;

import com.example.HospitalManagement.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("patientSecurity")
@RequiredArgsConstructor
public class PatientSecurity {

    private final PatientRepository patientRepository;

    public boolean isPatientOwner(Integer patientId,String username) {
        return patientRepository.existsByIdAndUserEntityUsername(patientId,username);
    }
}
