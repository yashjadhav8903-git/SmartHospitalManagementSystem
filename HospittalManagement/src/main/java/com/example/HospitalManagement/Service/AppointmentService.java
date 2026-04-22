package com.example.HospitalManagement.Service;

//import com.example.HospitalManagement.EmailServices.NormalEmailService;

import com.example.HospitalManagement.EmailServices.EmailEvent;
import com.example.HospitalManagement.EmailServices.NormalEmailService;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.MapStruct.AppointmentMapper;
import com.example.HospitalManagement.Rabbit_MQ.AppointmentBookEvent;
import com.example.HospitalManagement.Rabbit_MQ.BookingCancelEvent;
import com.example.HospitalManagement.Rabbit_MQ.BookingEventDTO;
import com.example.HospitalManagement.Rabbit_MQ.CancelEventDTO;
import com.example.HospitalManagement.Redis.AppointmentPageResponseDTO;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorSlotRepository doctorSlotRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final AppointmentMapper appointmentMapper;
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final EmailEvent emailEvent;
    private final NormalEmailService normalEmailService;
    private final RabbitTemplate rabbitTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;


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
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    @PreAuthorize("hasAuthority('Appointment:Write')")  //--> Ye hi hota hai Ownership Based Access Control
    public CreateAppointmentResponseDTO  CreateNewAppointments(CreateAppointmentRequestDTO requestDTO) throws IllegalAccessException, MessagingException {

        System.out.println(
                "Thread: " + Thread.currentThread() +
                        " | isVirtual: " + Thread.currentThread().isVirtual()
        );

            Integer slotId = requestDTO.getSlot();
            String lockKey = "lock:key:" + slotId;
            // Redisson lock
            RLock lock = redissonClient.getLock(lockKey);

            try {
                    // ager lock kisi or ke paas hai toh 10 second ke liye wait kro Nahi toh 5 second baat Auto relase hoga
                    boolean isLocked = lock.tryLock(10,TimeUnit.SECONDS);

                    // Agar 10 sec wait karne ke baad bhi lock nahi mila
                    if(!isLocked){
                        throw new RuntimeException("Slot is being booked by another user, please try again");
                    }
                    // 🔥 2. Fetch Slot
                    DoctorSlot doctorSlot =  doctorSlotRepository.findById(requestDTO.getSlot())
                            .orElseThrow(() -> new RuntimeException("Slot not found"));

                    // 🔥 3. Double check (IMPORTANT 💀)
                    if(doctorSlot.isBooked()){
                        throw new RuntimeException("Appointment already Booked");
                    }
                    // 🔥 4. Fetch Patient
                    Patient patient = patientRepository.findById(requestDTO.getPatientId())
                            .orElseThrow(() -> new RuntimeException("Patient Not Found at this id"));

                    // --> request to entity
                    Appointment appointment = appointmentMapper.UserToEntity(requestDTO);
                    appointment.setSlot(doctorSlot);
                    appointment.setDoctor(doctorSlot.getDoctor());
                    appointment.setPatient(patient);
                    appointment.setReason(requestDTO.getAppointmentReason());
    //                appointment.setAppointmentTime(LocalDateTime.of(doctorSlot.getDate(),doctorSlot.getStartTime()));
                    appointment.setStatus(AppointmentStatus.CONFIRMED); //Enum

                    // 🔥 6. Mark slot booked
                    doctorSlot.setBooked(true);
                    doctorSlotRepository.save(doctorSlot);

                    // For BioDirectional
                    patient.getAppointments().add(appointment);
                    // 🔥 7. Save
                Appointment saved = appointmentRepository.save(appointment);  // New Entry
                System.out.println("Appointment Successfully Book" + appointment);

                BookingEventDTO eventDTO = new BookingEventDTO();
                eventDTO.setId(saved.getId());
                eventDTO.setEmail(patient.getEmail());
                eventDTO.setUsername(patient.getName());
                eventDTO.setSlot(saved.getSlot().getId());
                eventDTO.setDocterName(doctorSlot.getDoctor().getName());
                eventDTO.setDate(doctorSlot.getDate());
                eventDTO.setAppointmentTime(LocalDateTime.parse(saved.getAppointmentTime().toString()));
                eventDTO.setReason(saved.getReason());

                // Event publish karo (Ye abhi RabbitMQ ko nahi bhej raha!)
                applicationEventPublisher.publishEvent(new AppointmentBookEvent(this,eventDTO));

                    return appointmentMapper.EntityToDTO(saved);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Booking process interrupted!");
            } finally {
                //Lock reliase
                //redisTemplate.delete(lockKey);
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
    }

    // --> CancelAppointment
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    @PreAuthorize("hasAuthority('Appointment:Delete')")
    public CancelAppointmentResponseDTO cancelAppointment(CancelAppointmentRequestDTO cancelDTO){

        // find Appointment
        Appointment appointment = appointmentRepository.findById(cancelDTO.getAppointmentId()).
                orElseThrow(() -> new RuntimeException("Appointment Not Found at this Id" + cancelDTO.getAppointmentId()));

        // security check
        if(!appointment.getPatient().getId().equals(cancelDTO.getPatientId())){
            System.out.println("you can't Cancel Other Appointment");
        }
        // past slot ko prevent ke liye
        if (appointment.getSlot().getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel past appointment");
        }

        // Already cancel Check
        if(appointment.getStatus() == AppointmentStatus.CANCELLED){
            throw new RuntimeException("Appointment Already Cancelled");
        }

        Integer slotId = appointment.getSlot().getId();
        String lockKey = "lock:key:" + slotId;

        // 🔥 1. Try to acquire lock (5 sec expiry)
        Boolean isLocked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey,"locked",5, TimeUnit.SECONDS);

        if(Boolean.FALSE.equals(isLocked)){
            throw new RuntimeException("try again");
        }
        try {
            DoctorSlot slot = appointment.getSlot();
            slot.setBooked(false); // lock release
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setReason(cancelDTO.getCancelReason());

            // DB mein update karo
            appointmentRepository.save(appointment);
            doctorSlotRepository.save(slot);
            // cancel Email
            CancelEventDTO eventDTO = new CancelEventDTO();
            eventDTO.setId(appointment.getId());
            eventDTO.setEmail(appointment.getPatient().getEmail());
            eventDTO.setPatientName(appointment.getPatient().getName());
            eventDTO.setSlot(appointment.getSlot().getId());
            eventDTO.setDocterName(appointment.getDoctor().getName());
            eventDTO.setDate(slot.getDate());
            eventDTO.setAppointmentTime(LocalDateTime.parse(appointment.getAppointmentTime().toString()));
            eventDTO.setReason(appointment.getReason());
            applicationEventPublisher.publishEvent(new BookingCancelEvent(this,eventDTO));
            return appointmentMapper.EntityToResponse(appointment);
        }
        finally {
            redisTemplate.delete(lockKey);
        }
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
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #doctorId == authentication.principal.id)")  //---> Ye hi hota hai Ownership Based Access Control
    public AppointmentPageResponseDTO<AppointmentResponseDTO> getAppointments(Pageable pageable, Integer doctorId){
        // get String key
        String key = "appointments::" + doctorId + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort().toString();
        // check key and fetch data from redis

        AppointmentPageResponseDTO <AppointmentResponseDTO> cached  =
                (AppointmentPageResponseDTO<AppointmentResponseDTO>) redisTemplate.opsForValue().get(key);

        if(cached != null){
            log.info("Fetching Data from Redis for Patient Page: {}", doctorId);
            return cached;
        }

        // ager nahi hai toh DB se lao
        log.info("Redis Cache miss  --> DB Hit : {}", doctorId);
        Page<Appointment> appointments = appointmentRepository.findAppointmentBydoctorId(doctorId, pageable);
        // page convert to list
        List<AppointmentResponseDTO> dtoList = appointments.getContent()
                .stream().map(appointmentMapper::EntityPagetoDTO)
                .toList();
        // list mapping
        AppointmentPageResponseDTO<AppointmentResponseDTO> responseDTO =
                new AppointmentPageResponseDTO<>(
                dtoList,
                appointments.getNumber(),
                appointments.getSize(),
                appointments.getTotalElements(),
                appointments.getTotalPages()
                );
        // save that list to redis
        redisTemplate.opsForValue().set(
                key,
                responseDTO,
                Duration.ofHours(1)
        );
        return responseDTO;
    }
}