package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v6/Insurance")
public class Insurance {

    private final InsuranceService insuranceService;

    @PostMapping("/assignInsurance/{patientId}")
    public ResponseEntity<AssignInsurancePatientResponseDTO> assignInsurance(@RequestBody AssignInsuranceToPatientsRequestDTO assignInsuranceToPatientsRequestDTO ,
                                                                             @PathVariable Integer patientId ){
        return ResponseEntity.ok(insuranceService.AssignInsurancePatient(assignInsuranceToPatientsRequestDTO,patientId));
    }
}
