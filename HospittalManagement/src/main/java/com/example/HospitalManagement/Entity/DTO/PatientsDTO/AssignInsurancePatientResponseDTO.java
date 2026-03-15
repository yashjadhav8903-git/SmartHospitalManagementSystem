package com.example.HospitalManagement.Entity.DTO.PatientsDTO;

import com.example.HospitalManagement.Enums.InsuranceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignInsurancePatientResponseDTO {

    private Integer patientId;
    private String patientName;
    private String providerName;
    private String policyNumber;
    private LocalDate validUntil;
    private InsuranceType insuranceType;
}
