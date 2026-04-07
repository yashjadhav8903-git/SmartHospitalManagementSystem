package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.Service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/A1/Admin/")
public class Admin {

    private final DoctorService doctorService;

    // --> PostAPI's
    @PostMapping("/DoctorOnBoarding")
    public ResponseEntity<DoctorPOSTResponseDTO> CreateNewDoctor( @RequestBody DoctorPOSTRequestDTO doctorPOSTRequestDTO){
        log.info("DoctorOnBoarding Request Received from : {}", doctorPOSTRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.onBoardDooctor(doctorPOSTRequestDTO));
    }
}
