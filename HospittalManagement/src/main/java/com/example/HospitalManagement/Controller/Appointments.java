package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Entity.Appointment;
import com.example.HospitalManagement.Redis.AppointmentPageResponseDTO;
import com.example.HospitalManagement.Service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/appointments")
@Tag(name = "Appointments-API's")
public class Appointments {

    private final AppointmentService appointmentService;


    // 1 --> getAppointmentWithProjection
    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "find Appointment by there Doctor id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') @appointmentSecurity.isDoctorOwner(#doctorId,authentication.name))")  //---> Ye hi hota hai Ownership Based Access Control
    public ResponseEntity<AppointmentPageResponseDTO<AppointmentResponseDTO>> getAppointmentByDoctorId(@PathVariable Integer doctorId,
                                                                                                       @PageableDefault(page = 0,
                                                                                                               size = 10,
                                                                                                               sort = "appointmentTime",
                                                                                                               direction = Sort.Direction.DESC) Pageable pageable){
        log.info("getAppointments Request Received from Doctor-Id : {}", doctorId);
        return ResponseEntity.ok(appointmentService.getAppointments(pageable, doctorId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "find Appointment by there Patient Id")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isPatientOwner(#patientId,authentication.name))")
    public ResponseEntity<AppointmentPageResponseDTO<AppointmentResponseDTO>> getAppointmentByPatientId(@PathVariable Integer patientId,
                                                                                                        @PageableDefault(page = 0, size = 10, sort = "appointmentTime",
                                                                                                                direction = Sort.Direction.DESC) Pageable pageable){
        log.info("getAppointments Request Received from Patient-Id : {}", patientId);

        AppointmentPageResponseDTO<AppointmentResponseDTO> patientAppointments =
                appointmentService.getPatientAppointments(pageable, patientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientAppointments);
    }

    //2 ---> CreateNewAppointment API's with MapStruct
    @PostMapping("/book-Appointment")
    @Operation(summary = "Book-Appointment's without Patient Register")
    @PreAuthorize("hasAuthority('Appointment:Write')")
    public ResponseEntity<CreateAppointmentResponseDTO> CreateAppointment(@RequestBody CreateAppointmentRequestDTO
                                                                                requestDTO) throws IllegalAccessException, MessagingException {

        log.info("Book-Appointment Request Received from Patient-Id & Doctor-Id : {}",requestDTO);
        CreateAppointmentResponseDTO appointment = appointmentService.
                    CreateNewAppointments(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointment);
    }


    //3 ---> ReAssign Appointment TO NewDoctor
    @PutMapping("/reAssign/{patientId}")
    @Operation(summary = "ReAssign Appointment TO NewDoctor using patient-ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentReAssignResponseDTO> ReAssignDoctor(@RequestBody AppointmentReAssignRequestDTO
                                                                     appointmentReAssignRequestDTO,
                                                         @PathVariable Integer patientId){

        log.info("ReAssign-Appointment Request Received from : {}", appointmentReAssignRequestDTO);

        AppointmentReAssignResponseDTO appointmentReAssignResponseDTO =
                appointmentService.reAssignAppointmentTOAnotherDoctor(appointmentReAssignRequestDTO, patientId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentReAssignResponseDTO);
    }


    //4 ---> Cancel Appointment
    @PostMapping("/cancel")
    @Operation(summary = "Cancel the Appointment")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity" +
            ".isAppointmentOwnerForPatient(#cancelAppointmentRequestDTO.appointmentId,authentication.name))")
    public ResponseEntity<CancelAppointmentResponseDTO> CancelAppointment(@RequestBody @NotNull CancelAppointmentRequestDTO
                                                                                      cancelAppointmentRequestDTO){

        log.info("Cancel-Appointment Request Received from Appointment-Id : {}",
                cancelAppointmentRequestDTO.getAppointmentId());

        CancelAppointmentResponseDTO cancelResponse =
                appointmentService.cancelAppointment(cancelAppointmentRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cancelResponse);
    }

    @PostMapping("/Reschedule")
    @Operation(summary = "Reschedule the Appointment to new slot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rescheduleAppointment (@RequestBody RescheduleRequestDTO
                                                                 rescheduleRequestDTO){

        log.info("rescheduleAppointment Request Received from Appointment-Id : {}",
                rescheduleRequestDTO.getAppointmentId());

        String response =
                appointmentService.rescheduleAppointment(rescheduleRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove Appointment Data form Record's By Admin Only.")
    public ResponseEntity<String> removeAppointment(@RequestParam Integer appointmentId,
                                                    @RequestParam Integer patientId) {

        log.info("removeAppointment Request Received from Appointment-Id : {}",appointmentId);

        appointmentService.RemoveAppointment(appointmentId, patientId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Appointment has been successfully removed");
    }
}