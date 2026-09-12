package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.AppointmentsDTO.AppointmentResponseDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorResponseDTOView;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Service.AppointmentService;
import com.example.HospitalManagement.Service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v4/doctors")
@Tag(name = "Doctors-API's")
public class Doctors {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    // ---> getDepartmentAndDoctorById
    @GetMapping("/{id}")
    @Operation(summary = "get doctor with Department by doctor id")
    @PreAuthorize("hasAuthority('Doctor:Read')")
    public ResponseEntity<Page<DoctorResponseDTOView>> getDoctorById(@PathVariable Integer doctorId,
                                                                     @RequestParam (defaultValue = "0")int page,
                                                                        @RequestParam (defaultValue = "2")int size){

        log.info("Fetch Doctor By Id Request Received from : {}", doctorId);

        Pageable pageable = PageRequest.of(page,size);
        Page<DoctorResponseDTOView> doctorsResponse = doctorService.getDoctorsById(doctorId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(doctorsResponse);
    }

    // --> Find All Doctor's
    @GetMapping
    @Operation(summary = "get all doctor information")
    @PreAuthorize("hasAuthority('Doctor:Read')")
    public ResponseEntity<Page<DoctorResponseDTOView>> getAllDoctors(@RequestParam (defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size){
        log.info("Fetch Doctor's Request Received from : {}", page);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    // 1 --> getAppointmentWithProjection
    @GetMapping("/appointments/{doctorId}")
    @Operation(summary = "get doctor appointment with doctor-Id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @appointmentSecurity.isDoctorOwner(#doctorId,authentication.name))")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointment(@PathVariable Integer doctorId){

        List<AppointmentResponseDTO> appointmentDoctor =
                appointmentService.getAppointmentDoctor(doctorId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentDoctor);
    }
}
