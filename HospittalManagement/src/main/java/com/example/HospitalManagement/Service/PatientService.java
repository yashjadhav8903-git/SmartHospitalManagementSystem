package com.example.HospitalManagement.Service;

//import com.example.HospitalManagement.EmailServices.NormalEmailService;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.MapStruct.PatientMapper;
import com.example.HospitalManagement.Projection.ForPatients.GetAllPatientProjection;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import com.example.HospitalManagement.Projection.ForPatients.PatientSummaryPage;
import com.example.HospitalManagement.Redis.PageResponseDTO;
import com.example.HospitalManagement.Redis.RedisConfig;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.SpringSecurity.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor


public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

//    private final NormalEmailService normalEmailService;

    @Transactional
    @PreAuthorize("hasAuthority('Patient:Read')")
    public PageResponseDTO<AllPatientDTO> getAllPatient(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        //*** --> patient key is pageNumber
        // only for 5 page caching
        if (pageNumber < 5) {
            String key = "Patient::" + pageNumber + ":" + pageable.getPageSize() + ":" + pageable.getSort().toString();

            // redis se data fetch
            PageResponseDTO<AllPatientDTO> cached = (PageResponseDTO<AllPatientDTO>) redisTemplate.opsForValue().get(key);

            if (cached != null) {
                log.info("Fetching Data from Redis for Patient Page: {}", pageNumber);
                return cached;   // Seedha DTO return karo
            }
            // redis miss
            log.info("Redis Cache miss  --> DB Hit : {}", pageNumber);
            Page<Patient> patients = patientRepository.findAll(pageable);
            // 4. Page ko DTO mein convert karo ( kyu ki page ko direct redis me nahi store sakte isliye list me convert kiya
            List<AllPatientDTO> dtoList = patients.getContent()
                    .stream().map(patientMapper::EntitytoDTO)
                    .toList();

            // save that data to PageResponseDTO
            PageResponseDTO<AllPatientDTO> dtoResponse = new PageResponseDTO<>(
                    dtoList,
                    patients.getNumber(),
                    patients.getSize(),
                    patients.getTotalElements(),
                    patients.getTotalPages()
                    );
            //Save Data in Redis Cache
            redisTemplate.opsForValue().set(
                    key,  // --> patient key
                    dtoResponse,  //--> set dtorespone instant of entire entity
                    Duration.ofHours(2)  // --> 2 hours ke baad data redis se delete ho jayega.
            );
            return dtoResponse;

        } else {
            // After 5 page Data come for DB
            log.info("Skipping Cache for page : {}", pageNumber);
            Page<Patient> patients = patientRepository.findAll(pageable);
            return (PageResponseDTO<AllPatientDTO>) patients.map(patientMapper::EntitytoDTO);
        }
    }


    @Transactional
    @PreAuthorize("hasAuthority('Patient:Read') and #patientid == authentication.principal.id")
    public AllPatientDTO getPatientById(Integer patientid) throws Exception {

        // manuall caching
        String key = "Patient::" + patientid; // --> patient Id is key

        //Redis check
        AllPatientDTO cachingPatient = (AllPatientDTO) redisTemplate.opsForValue().get(key);
        // Fetching Data from Redis
        if (cachingPatient != null) {
            log.info("Fetching Data from Redis for PatientId : {}", patientid);
            return cachingPatient;
        }

        log.info("Fetching Patient Data from DataBase for PatientId: {}", patientid);
        // Fetching Data from Database
        Patient patients = patientRepository.findById(patientid).orElseThrow(() ->
                new Exception("Patient Not Found at this Patient Id" + patientid));
        AllPatientDTO allPatientDTO = patientMapper.EntitytoDTO(patients);

        // save in redis
        redisTemplate.opsForValue().set(
                key,   // --> patient key
                allPatientDTO,  //--> save DTO instant of Entity
                Duration.ofHours(3)  //--> 3 hours ke baad data redis ke delete ho jayega.
        );
        return allPatientDTO;
    }


    // --> Post mapping
    @Transactional
    @PreAuthorize("hasAuthority('Patient:Write')")
    public PatientPostResponseDTO NewPatient(PatientPostRequestDTO patientPostRequestDTO, UserEntity adminUser) {
        //1
        UserEntity user = userRepository.findById(adminUser.getId());
        if (patientRepository.existsById(adminUser.getId())) {
            throw new RuntimeException("Patient already exists for this user");
        }
        Patient NewPatient = patientMapper.userToEnity(patientPostRequestDTO);
        NewPatient.setUserEntity(user);
        NewPatient.setCreatedBy(adminUser); // -->
        Patient patient = patientRepository.save(NewPatient);
        // Single patient cache delete
        redisTemplate.delete("Patient: " + patient.getId());

        // Delete Page wala data
        Set<String> keys = redisTemplate.keys("Patient::*");
        if(keys != null && !keys.isEmpty()){
           redisTemplate.delete(keys);
        }
        // upgrade role
        user.getRoles().add(RolesType.PATIENT);
        userRepository.save(user);
//        normalEmailService.sendHtmlEmail(patient.getEmail(),patient.getName());
        return patientMapper.EntityToUser(patient);



}
    //  ye hi toh projection ko DTO me Convert hota hai. (Interface Projection)
    public List<PatientInterfaceProjectionDTO> getPatientSummary(){
            return patientRepository.findPatientSummary(Sort.by(Sort.Direction.ASC, "id"))//--> projection method Convert into DTO/List and sort by ID
                    .stream()
                    .map(patient -> modelMapper.map(patient, PatientInterfaceProjectionDTO.class))
                    .toList();
    }
    // Interface Projection by ID
    public PatientInterfaceProjectionDTO getPatientSummaryById(Integer id){
            Patient patient = (Patient) patientRepository.findById(id).orElseThrow(()->
                    new EntityNotFoundException("Patient Not Found at this Patient Id" + id));
            return modelMapper.map(patient,PatientInterfaceProjectionDTO.class);
    }


    //ye hi toh projection ko DTO me Convert hota hai. (Constructor Projection)
    public List<PatientResponseConstructorDTO> getPatientConstructorSummary(){
            return patientRepository.findPatientSummaryConstructor(Sort.by(Sort.Direction.ASC,("id")))
                    .stream()
                    .map(patient -> modelMapper.map(patient, PatientResponseConstructorDTO.class))
                    .toList();

    }


    //pagination
//    public Page<PatientPageResponseDTO> getPatientWithPage(Pageable pageable){
//        Page<PatientPageResponseDTO> patientWithPage = patientRepository.findPatientWithPage(pageable);
//        return patientWithPage.map(patient ->
//                modelMapper.map(patient, PatientPageResponseDTO.class));
//    }


    // Same Logic Pagination
    public Page<PatientPageResponseDTO> getPatientWithPage (Pageable pageable){
            return patientRepository.findPatientWithPage(pageable);
    }

    // page with Interface
    public Page<PatientSummaryPage> getPatientWithPageInterface(Pageable pageable){
            return patientRepository.findPatientWithPageInterface(pageable);
    }

    // -->GetAllPatientWithInsuranceWithMapstruct
    @Transactional
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('Insurance:Read')")
    public Page<PatientInsuranceResponseDTO> getAllPatientWithInsurance(Pageable pageable){
            Page<PatientInsuranceProjection> page = patientRepository.getAllPatientWithInsurance(pageable);
            return page.map(patientMapper::toDTO);
    }


}
