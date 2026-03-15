package com.example.HospitalManagement.Entity.DTO.InsurancesDTO;

import com.example.HospitalManagement.Enums.InsuranceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignInsuranceToPatientsRequestDTO {

    private String providerName;
    private String policyNumber;
    private LocalDate validUntil;
    private InsuranceType insuranceType;
}
