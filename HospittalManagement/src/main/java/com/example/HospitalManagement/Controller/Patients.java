package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.PatientsDTO.*;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.SignUpResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Redis.PageResponseDTO;
import com.example.HospitalManagement.Service.PatientService;
import com.example.HospitalManagement.SpringSecurity.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v5/patients")
@Tag(name = "Patients-API's")
public class Patients {


    private final PatientService patientService;
    private final AuthService authService;


    @GetMapping
    @Operation(summary = "Get all Patient Information")
    public ResponseEntity<PageResponseDTO<AllPatientDTO>> getAllPatient(@RequestParam (defaultValue = "0") int page,
                                                                        @RequestParam (defaultValue = "10") int size){
        log.info("Get all patient information");

        Pageable pageable = PageRequest.of(page,size);
        PageResponseDTO<AllPatientDTO> allPatient = patientService.getAllPatient(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allPatient);
    }

    @GetMapping("/{id}")
    @Operation(summary = "find patient with patient-Id")
    public ResponseEntity<AllPatientDTO> getPatientById(@PathVariable Integer id) throws Exception {

        log.info("Getting patient with patient-Id {}", id);

        AllPatientDTO patientResponse = patientService.getPatientById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientResponse);
    }

    // --> post Mapping
    @PostMapping
    @Operation(summary = "OnBoarding new Patient's")
    public ResponseEntity<PatientPostResponseDTO> NewPatient(@NotNull Principal principal,
                                                             @Valid @RequestBody PatientPostRequestDTO
                                                                     patientPostRequestDTO){
        log.info("New patient request {}", patientPostRequestDTO);

        PatientPostResponseDTO patientPostResponseDTO =
                patientService.NewPatient(patientPostRequestDTO, principal.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientPostResponseDTO);
    }

    // -->GetAllPatientWithInsuranceWithMapstruct
    @GetMapping("/patientInsurance")
    @Operation(summary = "get All Patient with Insurance.")
    public ResponseEntity<Page<PatientInsuranceResponseDTO>> getAllPatientAndInsurance(@RequestParam(defaultValue = "0")int page,
                                                                                       @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page,size, Sort.Direction.ASC,"p.id");
        return ResponseEntity.ok(patientService.getAllPatientWithInsurance(pageable));
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "get PatientInsurance By Patient-Id")
    public ResponseEntity<PatientInsuranceResponseDTO> getPatientInsuranceByPatientId(@PathVariable Integer patientId) throws Exception {
        PatientInsuranceResponseDTO patientInsuranceById =
                patientService.getPatientInsuranceById(patientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientInsuranceById);
    }

    @PostMapping("/register")
    @Operation(summary = "Register as Patient and Book-Appointment easily.")
    public ResponseEntity<SignUpRegisterResponseDTO> patientRegister(@Valid @RequestBody PatientSignUpRequestDTO
                                                                                 requestDTO) {
        log.info("patient register request {}", requestDTO);

        SignUpRegisterResponseDTO signUpResponseDTO =
                authService.registerPatient(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(signUpResponseDTO);
    }

}
