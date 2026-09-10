package com.example.HospitalManagement.DTO.InsurancesDTO;

import com.example.HospitalManagement.Enums.InsuranceType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignInsuranceToPatientsRequestDTO {


    @NotNull(message = "Please select an insurance plan")
    private Long insurancePlanId;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

}
