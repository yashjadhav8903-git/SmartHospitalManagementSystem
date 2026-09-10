package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.AdminDTOs.AdminRequestDTOs;
import com.example.HospitalManagement.DTO.AdminDTOs.AdminResponseDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.Service.AdminService;
import com.example.HospitalManagement.Service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/A1/Admin")
@Tag(name = "Admin API's")
public class Admin {

    private final DoctorService doctorService;
    private final AdminService adminService;

    // --> PostAPI's
    @PostMapping
    @Operation(summary = "OnBoarding new Doctor by Admin Only.")
    public ResponseEntity<DoctorPOSTResponseDTO> CreateNewDoctor(@RequestBody DoctorPOSTRequestDTO
                                                                              doctorPOSTRequestDTO){
        log.info("DoctorOnBoarding Request Received from : {}", doctorPOSTRequestDTO.getUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctorService.onBoardDoctor(doctorPOSTRequestDTO));
    }

    @PostMapping("/onboarding")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "OnBoarding Admin's by Master Admin Only.")
    public ResponseEntity<AdminResponseDTO> addAdmin (@Valid @RequestBody AdminRequestDTOs
                                                                  adminRequestDTOs){

        log.info("AdminOnBoarding Request Received from : {}", adminRequestDTOs.getUsername());
        AdminResponseDTO ResponseDTO = adminService.onboarding(adminRequestDTOs, AuthProviderType.EMAIL);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO);
    }
}
