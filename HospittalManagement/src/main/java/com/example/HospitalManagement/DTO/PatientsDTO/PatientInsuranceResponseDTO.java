package com.example.HospitalManagement.DTO.PatientsDTO;

import com.example.HospitalManagement.DTO.InsurancesDTO.InsuranceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientInsuranceResponseDTO {

    private Integer patientId;
    private String patientName;
    private String patientEmail;
    private InsuranceResponseDTO insurance;
}
