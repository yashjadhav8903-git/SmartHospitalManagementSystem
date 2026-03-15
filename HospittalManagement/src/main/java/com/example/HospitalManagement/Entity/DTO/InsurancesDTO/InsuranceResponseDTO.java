package com.example.HospitalManagement.Entity.DTO.InsurancesDTO;

import com.example.HospitalManagement.Enums.InsuranceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceResponseDTO {

    private String provider;
    private InsuranceType insuranceType;
    private LocalDate validUntil;
    private LocalDateTime createAt;
}
