package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.DTO.InsurancesDTO.InsurancePlanResponseDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Service.InsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Insurances-API's")
@RequestMapping("api/v6/Insurance")

public class Insurance {

    private final InsuranceService insuranceService;


    @GetMapping("/plans")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "About Insurance Plans Details")
    public ResponseEntity<List<InsurancePlanResponseDTO>> getPlans() {
        List<InsurancePlanResponseDTO> insurancePlans =
                insuranceService.getInsurancePlans();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(insurancePlans);
    }

    @PostMapping("/patient/{patientId}")
    @Operation(summary = "Assign Insurance to the Patient")
    @PreAuthorize("hasAuthority('Insurance:Operations')")
    public ResponseEntity<AssignInsurancePatientResponseDTO> assignInsurance(@RequestBody AssignInsuranceToPatientsRequestDTO
                                                                                         requestDTO,
                                                                             @PathVariable Integer patientId){

        AssignInsurancePatientResponseDTO assignInsurancePatientResponseDTO =
                insuranceService.AssignInsurancePatient(requestDTO, patientId);

        return ResponseEntity
                .ok()
                .body(assignInsurancePatientResponseDTO);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Shut down that Insurance for some time")
    @PreAuthorize("hasAuthority('Insurance:Read') or @patientInsurance.isInsuranceOwner(#patientId, authentication.name)")
    public ResponseEntity<String> softInsuranceRemove(@RequestParam Integer patientId) {
        insuranceService.cancelInsuranceForPatient(patientId);
        return ResponseEntity.ok().body("Insurance policy cancelled successfully for patient ID: " + patientId);
    }


    @PostMapping("/reActiveInsurance")
    @Operation(summary = "ReActive that Insurance")
    @PreAuthorize("hasAuthority('Insurance:Read') or @patientInsurance.isInsuranceOwner(#patientId, authentication.name)")
    public ResponseEntity<AssignInsurancePatientResponseDTO> reActiveInsurance(@RequestParam Integer patientId) {
        AssignInsurancePatientResponseDTO assignInsurancePatientResponseDTO =
                insuranceService.reActiveInsurance(patientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assignInsurancePatientResponseDTO);
    }
}