package com.example.HospitalManagement.CustomSecurityEvaluator;

import com.example.HospitalManagement.Repository.PatientInsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("patientInsurance")
@RequiredArgsConstructor
public class InsuranceSecurity {

    private final PatientInsuranceRepository patientInsuranceRepository;

    public boolean isInsuranceOwner(Integer patientId,String username){
        return patientInsuranceRepository.existsByPatientIdAndPatientUserEntityUsername(patientId,username);
    }
}
