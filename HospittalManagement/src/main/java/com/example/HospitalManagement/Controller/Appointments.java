package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Service.AppointmentService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v2/appointments")
public class Appointments {

    private final AppointmentService appointmentService;

    // 1 --> getAppointmentWithProjection
    @GetMapping("getAppointments/{doctorId}")
    public ResponseEntity<Page<AppointmentResponseDTO>> getAppointment(@RequestParam (defaultValue = "0") int page ,
                                                                       @RequestParam(defaultValue = "5") int size,
                                                                       @PathVariable Integer doctorId){
        log.info("getAppointments Request Received from : {}", doctorId);
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.ASC,"appointmentTime"));
        return ResponseEntity.ok(appointmentService.getAppointments(pageable,doctorId));
    }


    //2 ---> CreateNewAppointment API's with MapStruct
    @PostMapping("/Book-Appointment/{doctorId}/{patientId}")
    public ResponseEntity<CreateAppointmentResponseDTO> CreateAppointment(@RequestBody CreateAppointmentRequestDTO
                                                                                createAppointmentRequestDTO,
                                                                          @PathVariable Integer doctorId,
                                                                          @PathVariable Integer patientId) throws IllegalAccessException, MessagingException {
        log.info("Book-Appointment Request Received from Patient-Id : {}", patientId);
        CreateAppointmentResponseDTO appointment = appointmentService.
                CreateNewAppointments(createAppointmentRequestDTO,doctorId,patientId);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(appointment);
    }


    //3 ---> ReAssign Appointment TO NewDoctor
    @PutMapping("/reAssign/{patientId}")
    public AppointmentReAssignResponseDTO ReAssignDoctor(@RequestBody AppointmentReAssignRequestDTO appointmentReAssignRequestDTO,
                                                         @PathVariable Integer patientId){
        log.info("ReAssign-Appointment Request Received from : {}", appointmentReAssignRequestDTO);
        return appointmentService.reAssignAppointmentTOAnotherDoctor(appointmentReAssignRequestDTO,patientId);
    }


    //4 ---> Cancel Appointment
    @PostMapping("/cancel")
    public CancelAppointmentResponseDTO CancelAppointment(@RequestBody CancelAppointmentRequestDTO cancelAppointmentRequestDTO){
        log.info("Cancel-Appointment Request Received from : {}", cancelAppointmentRequestDTO);
        return appointmentService.cancelAppointment(cancelAppointmentRequestDTO);
    }
}
