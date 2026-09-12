package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.PatientsDTO.AllPatientDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.PatientInsuranceResponseDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.PatientPostRequestDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.PatientPostResponseDTO;
import com.example.HospitalManagement.DTO.SpringSecurityDTO.SignUpRequestDTO;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.ExceptionHandling.DuplicateEmailIdResourceException;
import com.example.HospitalManagement.ExceptionHandling.PatientNotFoundException;
import com.example.HospitalManagement.MapStruct.PatientMapper;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import com.example.HospitalManagement.Redis.PageResponseDTO;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.RoleRepository;
import com.example.HospitalManagement.SpringSecurity.AuthService;
import com.example.HospitalManagement.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor


public class PatientService {
    private final RoleRepository roleRepository;

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Patient:Read')")
    @AuditLog(action = "Get_All:Patient", resource = "Patient")
    public PageResponseDTO<AllPatientDTO> getAllPatient(Pageable pageable) {

        int pageNumber = pageable.getPageNumber();
        //*** --> patient key is pageNumber
        // only for 5 pages caching after that data come to Database
        if (pageNumber <= 5) {
            String key = "Patient::" + pageNumber + ":" + pageable.getPageSize() + ":" + pageable.getSort().toString();

            // redis se data fetch
            Object rawData = redisTemplate.opsForValue().get(key);

            if (rawData != null) {
                log.info("Fetching Data from Redis for Patient Page: {}", pageNumber);
                return objectMapper.convertValue(
                        rawData,objectMapper.getTypeFactory()
                                .constructParametricType(
                                        PageResponseDTO.class,
                                        AllPatientDTO.class
                                )
                );
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
            // After 5 pages Data come for DB
            log.info("Skipping Cache for page : {}", pageNumber);
            Page<Patient> patients = patientRepository.findAll(pageable);

            List<AllPatientDTO> dtoList = patients.getContent()
                    .stream().map(patientMapper::EntitytoDTO)
                    .toList();

            return new PageResponseDTO<>(
                    dtoList,
                    patients.getNumber(),
                    patients.getSize(),
                    patients.getTotalElements(),
                    patients.getTotalPages()
            );
        }
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "patients",key = "#patientId")
    @AuditLog(action = "Get_Patient:Id", resource = "Patient")
    @PreAuthorize("hasAuthority('Insurance:Operations') or @patientSecurity.isPatientOwner(#patientId,authentication.name)")
    public AllPatientDTO getPatientById(Integer patientId) throws Exception {

        log.info("Fetching Patient Data from Database for PatientId: {}", patientId);

        // Fetching Data from Database
        Patient patients = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException("Patient Not Found at this Patient Id : " + patientId));
        return patientMapper.EntitytoDTO(patients);

    }


    // --> Post mapping
    @Transactional
    @CacheEvict(value = "patients",allEntries = true)
    @PreAuthorize("hasAuthority('Patient:Write')")
    @AuditLog(action = "OnBoarding_Patient", resource = "Patient")
    public PatientPostResponseDTO NewPatient(PatientPostRequestDTO dto, String loggedUser) {
        //1
        UserEntity currentUser = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new RuntimeException("User not found: " + loggedUser));;

        // 2. Check if Admin is creating for someone else OR user is creating their own profile
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRolesName().equals(RolesType.ADMIN));

        UserEntity targetUser = isAdmin
                ? userRepository.findByUsername(dto.getEmail())
                .orElseGet(() -> authService.signupInternal(
                        new SignUpRequestDTO(
                                dto.getEmail(),
                                "TempPassword@123",
                                dto.getName()),
                        AuthProviderType.EMAIL,
                        null
                ))
                : currentUser;

        if (patientRepository.existsById(targetUser.getId())) {
            throw new DuplicateEmailIdResourceException("This Email-ID " + dto.getEmail() + " address is already associated with an existing profile. " +
                    "Please use a different email or Sign In with your current details.");
        }
        Patient NewPatient = patientMapper.userToEnity(dto);
        NewPatient.setUserEntity(targetUser);
        NewPatient.setCreatedBy(currentUser); // -->
        Patient patient = patientRepository.save(NewPatient);
        // Single patient cache delete
        redisTemplate.delete("Patient::" + patient.getId());

        return patientMapper.EntityToUser(patient);

}

    // -->GetAllPatientWithInsuranceWithMapstruct
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Insurance:Read')")
    public Page<PatientInsuranceResponseDTO> getAllPatientWithInsurance(Pageable pageable){
            Page<PatientInsuranceProjection> page = patientRepository.getAllPatientWithInsurance(pageable);
            return page.map(patientMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Insurance:Read') or @patientSecurity.isPatientOwner(#patientId,authentication.name)")
    public PatientInsuranceResponseDTO getPatientInsuranceById(Integer patientId) throws Exception {
        PatientInsuranceProjection projection = patientRepository.getPatientInsuranceById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient Insurance not found for ID: " + patientId));

        return patientMapper.toDTO(projection);
    }
}
