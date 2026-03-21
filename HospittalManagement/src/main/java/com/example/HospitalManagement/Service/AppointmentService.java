package com.example.HospitalManagement.Service;

//import com.example.HospitalManagement.EmailServices.NormalEmailService;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.MapStruct.AppointmentMapper;
import com.example.HospitalManagement.Projection.ForAppointmens.AppointmentProjection;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final AppointmentMapper appointmentMapper;
//    private final NormalEmailService normalEmailService;


    /// 2 ---> ReAssign Appointment TO NewDoctor
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #doctorId == authentication.principal.id)")
    public AppointmentReAssignResponseDTO reAssignAppointmentTOAnotherDoctor(AppointmentReAssignRequestDTO requestDTO, Integer patientId){

        // --> Find Patient
        Patient patients = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not Found"));
        // 1 --> Fetch Appointment
        Appointment appointment = appointmentRepository.findById(requestDTO.getAppointmentId()).
                        orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        //2 --> fetch Doctor
        Doctor newdoctor = doctorRepository.findById(requestDTO.getNewDoctorId()).
                orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // 3 --> ReAssign Logic ( new Doctor )
        appointment.setDoctor(newdoctor);  //--> this will automatically call the update because it is dirty now.
        newdoctor.getAppointments().add(appointment); // just for Bidirectional
        return appointmentMapper.EntityToUser(appointment);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #doctorId == authentication.principal.id)")  //---> Ye hi hota hai Ownership Based Access Control
    public Appointment RemoveAppointment(int appointmentId, Integer patientId){
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(()->
                new RuntimeException("No Appointment Found"));
        Patient patient = patientRepository.findById(patientId).orElseThrow(() ->
                new RuntimeException("No Patient Found"));

        patient.getAppointments().remove(appointment);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPatient(null);
         return appointment;
    }

    @Transactional
    public void RemoveBulkOfAppointments(Integer patientId, List<Integer>appointmentId){

        Patient patient = patientRepository.findById(patientId).orElseThrow(() ->
                new RuntimeException("No Patient Found"));

        patient.getAppointments().removeIf( appointment
                -> appointmentId.contains(appointment.getId()));
    }





    /// 1 ---> Create Appointment
//    @Transactional
    @CacheEvict(cacheNames = "appointments",allEntries = true)
    @PreAuthorize("hasAuthority('Appointment:Write')")  //--> Ye hi hota hai Ownership Based Access Control
    public CreateAppointmentResponseDTO  CreateNewAppointments(CreateAppointmentRequestDTO requestDTO, Integer doctorId, Integer patientId) throws IllegalAccessException, MessagingException {
        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(()-> new RuntimeException("Doctor Not Found at this ID"));

        Patient patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient Not Found at this id"));


        // ---> AutoAppointmentSet
        boolean isDoctorIsBusy=
                appointmentRepository.existsByDoctorIdAndAppointmentTimeAndStatusIn(
                doctorId,
                requestDTO.getAppointmentTime(),
        List.of(AppointmentStatus.CONFIRMED,AppointmentStatus.PENDING)
                );

        // --> request to entity
        Appointment appointment = appointmentMapper.UserToEntity(requestDTO);

        if(appointment.getId() != null) throw new IllegalAccessException("Appointment should not have ID");
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(AppointmentStatus.PENDING); //Enum

        // --> ifDoctorIsBusy
        if(isDoctorIsBusy){
            appointment.setStatus(AppointmentStatus.PENDING);
        } else {
            appointment.setStatus(AppointmentStatus.CONFIRMED);
        }
        // For BioDirectional
        patient.getAppointments().add(appointment);
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.EntityToDTO(saved);


        // Email ka Logic
//        if(saved.getStatus() == AppointmentStatus.CONFIRMED){
//            System.out.println("Status is CONFIRMED, sending email...");
//            normalEmailService.SendConfirmedEmail(
//                    patient.getEmail(),   // 1. Email
//                    patient.getName(),    // 2. Patient ka naam
//                    doctor.getName(),     // 3. Doctor ka naam
//                    saved.getId(),        // 4 .ID
//                    saved.getAppointmentTime().toString(), // 5. Time
//                    saved.getReason()     // 6. Bimari/Reason
//            );
//        } else if(saved.getStatus() == AppointmentStatus.PENDING) {
//            System.out.println("Status is PENDING, sending email...");
//            normalEmailService.SendPendingEmail(
//                    patient.getEmail(),
//                    patient.getName(),
//                    saved.getId(),
//                    saved.getReason()
//            );
//        }
//        return appointmentMapper.EntityToDTO(saved);
    }

    // --> CancelAppointment
    @Transactional
    @PreAuthorize("hasAuthority('Appointment:Delete')")
    public CancelAppointmentResponseDTO cancelAppointment(CancelAppointmentRequestDTO cancelDTO){

        // find Appointment
        Appointment appointment = appointmentRepository.findById(cancelDTO.getAppointmentId()).
                orElseThrow(() -> new RuntimeException("Appointment Not Found at this Id" + cancelDTO.getAppointmentId()));

        // security check
        if(!appointment.getPatient().getId().equals(cancelDTO.getPatientId())){
            System.out.println("you can't Cancel Other Appointment");
        }

        // Already cancel Check
        if(appointment.getStatus() == AppointmentStatus.CANCELLED){
            System.out.println("Appointment Already Cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setReason(cancelDTO.getCancelReason());
        return appointmentMapper.EntityToResponse(appointment);
    }


    //--> getAppointmetByDoctorId
    @Transactional
    @PreAuthorize("hasAuthority('Appointment:Read') and #doctorId == authentication.principal.id")
    public List<AppointmentResponseDTO> getAppointmntsofDoctor(Integer docterId){
        Doctor doctor = doctorRepository.findById(docterId).orElseThrow(() -> new RuntimeException("Doctor with ID " + docterId + " nahi mila!"));
        return doctor.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    //---> getAppointmentWithProjection
    @Transactional
    @Cacheable(cacheNames = "appointments" , key = "#'doctorId:' + #pageable.pageNumber + ':' + #pageable.pageSize ")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #doctorId == authentication.principal.id)")  //---> Ye hi hota hai Ownership Based Access Control
    public Page<AppointmentResponseDTO> getAppointments(Pageable pageable,Integer doctorId){
        System.out.println("🔥 DATABASE HIT - Fetching from DB");
        Page<AppointmentProjection> page = appointmentRepository.findAppointmentWithPage(doctorId, pageable);
        return page.map(appointmentMapper::ProjectionToResSTO);
    }
}