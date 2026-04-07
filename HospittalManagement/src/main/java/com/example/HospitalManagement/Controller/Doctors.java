package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.AppointmentResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorResponseDTOView;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Service.AppointmentService;
import com.example.HospitalManagement.Service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v4/doctors")
public class Doctors {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    // ---> getDepartmentAndDoctorById
    @GetMapping("/doctor/{id}")
    public ResponseEntity<Page<DoctorResponseDTOView>> getDoctorById(
            @PathVariable Integer id,
            @RequestParam (defaultValue = "0")int page,
            @RequestParam (defaultValue = "2")int size){
        log.info("Fetch Doctor By Id Request Received from : {}", id);
        Pageable pageable = PageRequest.of(page,size);
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(doctorService.getDoctorsById(user.getId(), pageable));
    }

    // --> Find All Doctor's
    @GetMapping("/Doctors")
    public ResponseEntity<Page<DoctorResponseDTOView>> getAllDoctors(@RequestParam (defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size){
        log.info("Fetch Doctor's Request Received from : {}", page);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.getAllDoctors(pageable));
    }

    // 1 --> getAppointmentWithProjection
    @GetMapping("appointments/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointment(//@RequestParam (defaultValue = "1") int page ,
                                                                       //@RequestParam(defaultValue = "3") int size,
                                                                       @PathVariable Integer doctorId){
//        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC,"appointmentTime"));
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  //---> Jo Login Doctor hai wo bas apni hai DoctorInformation dhek payega dusro ki nahi (isni login kiya hai wo)
//        return ResponseEntity.ok(<AppointmentResponseDTO> appointmentService.getAppointmntsofDoctor(user.getId());
        return ResponseEntity.ok(appointmentService.getAppointmntsofDoctor(user.getId()));
    }
}
