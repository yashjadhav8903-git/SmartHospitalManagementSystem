package com.example.HospitalManagement.DTO.InsurancesDTO;

import com.example.HospitalManagement.Enums.InsuranceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlanResponseDTO {

    private Long id;
    private String provider;
    private String planName;
    private Integer validateInMonths;   // e.g. 12 months ya 1 year's
    private InsuranceType insuranceType;
}
