package com.example.HospitalManagement.Service;



import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Entity.Appointment;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.ExceptionHandling.AppointmentNotFoundException;
import com.example.HospitalManagement.ExceptionHandling.DoctorNotFoundException;
import com.example.HospitalManagement.ExceptionHandling.PatientNotFoundException;
import com.example.HospitalManagement.MapStruct.AppointmentMapper;
import com.example.HospitalManagement.Rabbit_MQ.*;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.BookingEventDTO;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.CancelEventDTO;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.RescheduleEventDTO;
import com.example.HospitalManagement.Redis.AppointmentPageResponseDTO;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    /**
     * "Jahan multiple shared resources ek single business transaction mein
     * concurrent write/update hote hain, wahan un sabhi resources par locks acquire hone chahiye."
     */

    private final AppointmentRepository appointmentRepository;
    private final DoctorSlotRepository doctorSlotRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AppointmentMapper appointmentMapper;
    private final RedissonClient redissonClient;
    private final ApplicationEventPublisher applicationEventPublisher;


    /// 2 ---> ReAssign Appointment TO NewDoctor
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(action = "ReAssignAppointment" , resource = "Appointment")
    public AppointmentReAssignResponseDTO reAssignAppointmentTOAnotherDoctor(AppointmentReAssignRequestDTO
                                                                                         requestDTO, Integer patientId){

        String appLockKey = "lock:appointment:" + requestDTO.getAppointmentId();
        String doctorLock = "doctor:lock:" + requestDTO.getNewDoctorId();

        RLock appLock = redissonClient.getLock(appLockKey);
        RLock doctorLockId = redissonClient.getLock(doctorLock);

        RLock multipleLocks = redissonClient.getMultiLock(appLock,doctorLockId);

        try{

            boolean isLocked = multipleLocks.tryLock(5,10,TimeUnit.SECONDS);

            if(!isLocked){
                throw new LockedException("Lock has been acquired already.");
            }

                // 1 --> Fetch Appointment
                Appointment appointment = appointmentRepository.findById(requestDTO.getAppointmentId()).
                        orElseThrow(() -> new AppointmentNotFoundException("Appointment not found at this Appointment ID " + requestDTO.getAppointmentId()));

                //2 --> fetch Doctor
                Doctor newdoctor = doctorRepository.findById(requestDTO.getNewDoctorId()).
                        orElseThrow(() -> new DoctorNotFoundException("Doctor not found at this Doctor ID " + requestDTO.getNewDoctorId()));

                // 3 --> ReAssign Logic ( new Doctor )
                appointment.setDoctor(newdoctor);  //--> this will automatically call the update because it is dirty now.
                newdoctor.getAppointments().add(appointment); // just for Bidirectional

                return appointmentMapper.EntityToUser(appointment);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (multipleLocks.isHeldByCurrentThread()) multipleLocks.unlock();
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")  //---> Ye hi hota hai Ownership Based Access Control
    @AuditLog(action = "Hard Delete Appointment" , resource = "Admin_Hard_Delete")
    public void RemoveAppointment(Integer appointmentId, Integer patientId){
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->
                        new AppointmentNotFoundException("No Appointment Found, Please try later..."));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                new PatientNotFoundException("No Patient Found, Please try Later..."));

        patient.getAppointments().remove(appointment);
        appointment.setStatus(AppointmentStatus.DELETED);
        appointment.setPatient(null);
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
    @AuditLog(action = "CREATE_APPOINTMENT", resource = "Appointment")
    public CreateAppointmentResponseDTO CreateNewAppointments(CreateAppointmentRequestDTO requestDTO) throws IllegalAccessException, MessagingException {

        // 1.***IMP Current Authenticated User Fetch Karo
        String currentUsername = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        log.info("Request come to AppointmentBook Service with patient Id :  {} ",currentUsername);

        // 2. UserRepository se UserEntity load karo (Business Requirement ke liye)
        UserEntity currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated User not found in DB"));

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
                    Patient patient = patientRepository.findByUserEntityId(currentUser.getId())
                            .orElseGet(() -> {
                               Patient newPatient = Patient.builder()
                                       .name(currentUser.getName())
                                       .email(currentUser.getUsername())
                                       .userEntity(currentUser)
                                       .createdBy(currentUser)
                                       .build();

                               return patientRepository.save(newPatient);
                            });

                    // --> request to entity
                    Appointment appointment = appointmentMapper.UserToEntity(requestDTO);
                    appointment.setSlot(doctorSlot);
                    appointment.setDoctor(doctorSlot.getDoctor());
                    appointment.setPatient(patient);
                    appointment.setReason(requestDTO.getAppointmentReason());
                    //**IMP
                    appointment.setStatus(AppointmentStatus.PENDING_PAYMENT); //Enum

                    // Temporarily mark slot booked (Slot Hold)
                    doctorSlot.setBooked(true);
                    doctorSlotRepository.save(doctorSlot);

                    // For BioDirectional
                    if(patient.getAppointments() != null) {
                    patient.getAppointments().add(appointment);
                    }

                    // 🔥 7. Save
                    Appointment saved = appointmentRepository.save(appointment);

                /** IMP
                 * why we save appointment here ?
                 * 1.Razorpay ko order_id banane ke liye ek appointment_id ki zaroorat hoti hai.
                 * Agar DB me pehle save nahi karenge, toh Database ID generate nahi karega (@GeneratedValue).Status yahan PENDING_PAYMENT rehta hai.
                 * 2.but Hibernate ka Dirty Checking mechanism automatic handle kr sakta hai lekin ye good practice hai.
                 */

                return appointmentMapper.EntityToDTO(saved);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Booking process interrupted!");
            } finally {
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
    }

    // --> CancelAppointment
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity" +
            ".isAppointmentOwnerForPatient(#cancelDTO.appointmentId,authentication.name))")
    @AuditLog(action = "CANCEL_APPOINTMENT", resource = "Appointment")
    public CancelAppointmentResponseDTO cancelAppointment(CancelAppointmentRequestDTO cancelDTO){

        // 1.***IMP Current Authenticated User Fetch Karo
        UserEntity currentUser = (UserEntity) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // find Appointment
        Appointment appointment = appointmentRepository.findById(cancelDTO.getAppointmentId()).
                orElseThrow(() -> new AppointmentNotFoundException("Appointment Not Found at this Id " + cancelDTO.getAppointmentId()));

        // security check
        if(!appointment.getPatient().getId().equals(currentUser.getId())){
            throw new RuntimeException("you can't Cancel Other Appointment");
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
        // Lock both Appointment (to prevent double cancel) and Slot (to safely free slot)
        String appointmentLockKey = "lock:appointment:" + appointment.getId();
        String slotLockKey = "lock:key:" + slotId;

        RLock appointmentLock = redissonClient.getLock(appointmentLockKey);
        RLock slotLock = redissonClient.getLock(slotLockKey);

        // Redisson MultiLock locks both resources atomically
        RLock multipleLock = redissonClient.getMultiLock(appointmentLock,slotLock);


        try {

            // Try to acquire both locks within 5 seconds
            boolean isLocked = multipleLock.tryLock(5,10,TimeUnit.SECONDS);

            if(!isLocked){
                throw new RuntimeException("Cancellation request is already in progress. Please try again!");
            }

            // Re-check status inside lock (Double-checking pattern)
            if (AppointmentStatus.CANCELLED.equals(appointment.getStatus())) {
                throw new RuntimeException("Appointment is already cancelled!");
            }

            DoctorSlot slot = appointment.getSlot();
            slot.setBooked(false); // slot free

            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setReason(cancelDTO.getCancelReason());

            // DB mein update karo
            Appointment saved = appointmentRepository.save(appointment);
            DoctorSlot saveSlot = doctorSlotRepository.save(slot);

            // cancel Email
            CancelEventDTO eventDTO = cancelEmail(saved, saveSlot, appointment);
            applicationEventPublisher.publishEvent(new BookingCancelEvent(this,eventDTO));

            return appointmentMapper.EntityToResponse(appointment);
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cancellation process was interrupted!");
        }
        finally {
            if (multipleLock.isHeldByCurrentThread()) {
                multipleLock.unlock();
            }
        }
    }

    @NotNull
    private static CancelEventDTO cancelEmail(Appointment saved, DoctorSlot saveSlot, Appointment appointment) {
        CancelEventDTO eventDTO = new CancelEventDTO();
        eventDTO.setId(saved.getId());
        eventDTO.setEmail(saved.getPatient().getEmail());
        eventDTO.setPatientName(saved.getPatient().getName());
        eventDTO.setSlot(saved.getSlot().getId());
        eventDTO.setDocterName(saved.getDoctor().getName());
        eventDTO.setDate(saveSlot.getDate());
        eventDTO.setAppointmentTime(LocalDateTime.parse(appointment.getAppointmentTime().toString()));
        eventDTO.setReason(saved.getReason());
        return eventDTO;
    }



    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @appointmentSecurity.isDoctorOwner(#doctorId,authentication.name))")
    public List<AppointmentResponseDTO> getAppointmentDoctor(Integer doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor with ID " + doctorId + " nahi mila!"));
        return doctor.getAppointments()
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "appointments",
            key = "#doctorId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()"
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @appointmentSecurity.isDoctorOwner(#doctorId,authentication.name))")  //---> Ye hi hota hai Ownership Based Access Control
    public AppointmentPageResponseDTO<AppointmentResponseDTO> getAppointments(Pageable pageable, Integer doctorId){

        log.info("Redis Cache Miss --> DB Hit for Doctor ID: {}", doctorId);

        Page<Appointment> appointments = appointmentRepository
                .findAppointmentBydoctorId(doctorId, pageable);

        // page convert to list
        List<AppointmentResponseDTO> dtoList = appointments.getContent()
                .stream()
                .map(appointmentMapper::EntityPagetoDTO)
                .toList();

              return new AppointmentPageResponseDTO<>(
                dtoList,
                appointments.getNumber(),
                appointments.getSize(),
                appointments.getTotalElements(),
                appointments.getTotalPages()
                );
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "patient_appointment",
            key = "#patientId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
    )
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @appointmentSecurity.isPatientOwner(#patientId,authentication.name))")
    public AppointmentPageResponseDTO<AppointmentResponseDTO> getPatientAppointments(Pageable pageable,Integer patientId){

        log.info("Fetching paginated appointments for Patient ID: {} | Page: {}, Size: {}",
                patientId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Appointment> appointmentPage = appointmentRepository.findAppointmentByPatientId(patientId,pageable);

        List<AppointmentResponseDTO> dtoList = appointmentPage.getContent()
                .stream()
                .map(appointmentMapper::EntityPagetoDTO)
                .toList();

        return new AppointmentPageResponseDTO<>(
                dtoList,
                appointmentPage.getNumber(),
                appointmentPage.getSize(),
                appointmentPage.getTotalElements(),
                appointmentPage.getTotalPages()
        );
    }


    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    @AuditLog(action = "Reschedule_Appointment" , resource = "Appointment")
    @PreAuthorize("hasRole('ADMIN')")
    public String rescheduleAppointment(RescheduleRequestDTO requestDTO){

        log.info("Request come to rescheduleAppointment Service with Appointment - ID : {}", requestDTO.getAppointmentId());

        Appointment tempAppointment = appointmentRepository.findById(requestDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment with ID " + requestDTO.getAppointmentId() + " not found!"));


        Integer oldSlotId = tempAppointment.getSlot() != null ? tempAppointment.getSlot().getId() : null;
        Integer newSlotId = requestDTO.getNewSlotId();

        // lock on 3 resource
        String appointmentLockKey = "lock:appointment:" + requestDTO.getAppointmentId();
        String oldSlotKey = "lock:key1:" + oldSlotId;
        String newSlotKey = "lock:key2:" + newSlotId;

        RLock appLock = redissonClient.getLock(appointmentLockKey);
        RLock oldSlotLock = redissonClient.getLock(oldSlotKey);
        RLock newSlotLock = redissonClient.getLock(newSlotKey);

        // Combine into MultiLock
        RLock multiple = redissonClient.getMultiLock(appLock,oldSlotLock,newSlotLock);


        try {

            boolean isLocked = multiple.tryLock(7, 10,TimeUnit.SECONDS);

            if(!isLocked){
                throw new RuntimeException("Selected slot is currently being processed by another user. Please try again!");
            }

            // 1. Existing Appointment fetch karo
            Appointment appointment = appointmentRepository.findById(requestDTO.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment with ID " + requestDTO.getAppointmentId() + " not found!"));


            // Check if appointment is already canceled
            if (AppointmentStatus.CANCELLED.equals(appointment.getStatus())) {
                throw new RuntimeException("Cannot reschedule a cancelled appointment!");
            }

            // 2. Target New Slot fetch karo
            DoctorSlot newSlot = doctorSlotRepository.findById(requestDTO.getNewSlotId())
                    .orElseThrow(() -> new RuntimeException("Selected new slot is already booked by another patient!"));


            // Check karo ki Naya Slot free hai ya nahi
            if (newSlot.isBooked()) {
                throw new RuntimeException("Selected new slot is already booked by another patient!");
            }


            // 3. Purane Slot ko FREE karo
            DoctorSlot oldSlot = appointment.getSlot();
            if (oldSlot != null) {
                oldSlot.setBooked(false);
                doctorSlotRepository.save(oldSlot);
            }

            // 4. Naye Slot ko LOCK karo
            newSlot.setBooked(true);
            doctorSlotRepository.save(newSlot);

            // 5. Appointment me naya slot set karo aur save karo
            appointment.setSlot(newSlot);
            Appointment updatedAppointment = appointmentRepository.save(appointment);

            //  ye just email formating ke liye hai ok
            // AM/PM Formatter (e.g., "11:30 AM")
            DateTimeFormatter amPmFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            String formattedStartTime = newSlot.getStartTime().format(amPmFormatter);
            String formattedEndTime = newSlot.getEndTime().format(amPmFormatter);

            // Doctor Name formatting
            String doctorFormattedName = newSlot.getDoctor().getName().startsWith("Dr.")
                    ? newSlot.getDoctor().getName()
                    : "Dr. " + newSlot.getDoctor().getName();

            // send to rabbitMq Event
            RescheduleEventDTO eventDTO = RescheduleEventDTO.builder()
                    .appointmentId(updatedAppointment.getId())
                    .email(updatedAppointment.getPatient().getEmail())
                    .patientName(updatedAppointment.getPatient().getName())
                    .doctorName(doctorFormattedName)
                    .newDate(newSlot.getDate())
                    .startTime(formattedStartTime)
                    .endTime(formattedEndTime)
                    .build();

            applicationEventPublisher.publishEvent(new AppointmentRescheduleEvent(this, eventDTO));

            return "Appointment successfully rescheduled to Date: " + newSlot.getDate() + " & Time: " + newSlot.getStartTime();


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Reschedule process interrupted!");
        } finally {
            if (multiple.isHeldByCurrentThread()) {
                multiple.unlock();
            }
        }
    }

    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    @AuditLog(action = "Confirmed_AppointmentPayment", resource = "Appointment_Payment_Confirmation")
    public void confirmedAppointmentAfterPayment(Integer appointmentId){

        log.info("request come to ConfirmedAppointmentPayment with appointment ID: {} ", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

        // Status updated to CONFIRMED
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = appointmentRepository.save(appointment);

        log.info("Appointment with ID : {} has been saved successfully! Appointment Status is : {}", appointment.getId(), saved.getStatus());

        // RabbitMQ Event for Email Notification
        BookingEventDTO eventDTO = createEventDTO(saved);

        applicationEventPublisher.publishEvent(new AppointmentBookEvent(this, eventDTO));
    }

    @NotNull
    private static BookingEventDTO createEventDTO(Appointment saved) {

        BookingEventDTO eventDTO = new BookingEventDTO();
        eventDTO.setId(saved.getId());
        eventDTO.setEmail(saved.getPatient().getEmail());
        eventDTO.setUsername(saved.getPatient().getName());
        eventDTO.setSlot(saved.getSlot().getId());
        eventDTO.setDocterName(saved.getDoctor().getName());
        eventDTO.setDate(saved.getSlot().getDate());
        eventDTO.setAppointmentTime(LocalDateTime.parse(saved.getAppointmentTime().toString()));
        eventDTO.setReason(saved.getReason());
        return eventDTO;

    }
}