package com.example.HospitalManagement.Service;

//import com.example.HospitalManagement.EmailServices.NormalEmailService;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.MapStruct.PatientMapper;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import com.example.HospitalManagement.Projection.ForPatients.PatientSummaryPage;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor


public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PatientMapper patientMapper;
    private final UserRepository userRepository;
//    private final NormalEmailService normalEmailService;

    @Transactional
    @PreAuthorize("hasAuthority('Patient:Read')")
    public List<AllPatientDTO> getAllPatient(){
            List<Patient> patients = patientRepository.findAll();
            return patients.
                    stream()
                    .map(patient -> modelMapper.map(patient,AllPatientDTO.class))
                    .toList();
    }

        @Transactional
        @Cacheable(cacheNames = "patient",key = "#patientid")
        @PreAuthorize("hasAuthority('Patient:Read') and #patientid == authentication.principal.id")
        public AllPatientDTO getPatientById(Integer patientid) throws Exception {
        log.info("Fetching Patient with ID: {}",patientid);
            System.out.println("🔥 DATABASE HIT - Fetching Patient for PatientId : " + patientid);
        Patient patients = patientRepository.findById(patientid).orElseThrow(()->
                new Exception("Patient Not Found at this Patient Id" + patientid));
        return (AllPatientDTO) modelMapper
                .map(patients,AllPatientDTO.class);
        }


    // --> Post mapping
    @Transactional
    @CacheEvict(cacheNames = "patient",allEntries = true)
    @PreAuthorize("hasAuthority('Patient:Write')")
    public PatientPostResponseDTO Newpatient(PatientPostRequestDTO patientPostRequestDTO,UserEntity adminUser){
        //1
        UserEntity user = userRepository.findById(adminUser.getId());
        if(patientRepository.existsById(adminUser.getId())){
            throw new RuntimeException("Patient already exists for this user");
        }
        Patient NewPatient = patientMapper.userToEnity(patientPostRequestDTO);
        NewPatient.setUserEntity(user);
        NewPatient.setCreatedBy(adminUser); // -->
        Patient patient = patientRepository.save(NewPatient);
        // upgrade role
        user.getRoles().add(RolesType.PATIENT);
        userRepository.save(user);
//        normalEmailService.sendHtmlEmail(patient.getEmail(),patient.getName());
        return patientMapper.EntityToUser(patient);


        //2
//        if(patientRepository.existsById(currentUser.getId())){
//            throw new RuntimeException("Patient already exists for this user");
//        }
//
//        // 1. DTO ko Entity mein convert karo
//        Patient newPatient = patientMapper.userToEnity(patientPostRequestDTO);
//
//        // 2. Logged-in User ki Entity nikaalo
//        UserEntity managedUser = (UserEntity) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        // 3. IMPORTANT: UserEntity ko current persistence context mein lao
//        // Agar primary key 'id' hai, toh userRepository se fetch kar lo
//        UserEntity managedUser = userRepository.findById(currentUser.getId());
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 3. Link establish karo (Sabse important step)
//        newPatient.setUserEntity(managedUser);
//        // Debugging point
//        System.out.println("DEBUG: Patient ID before save: " + newPatient.getId());
//
//        // Agar ye 0 ya koi number print kar raha hai, toh wahi error hai.
//        // Isse manually null karke dekho:
//        newPatient.setId(null);
//
//        // 4. Ab save karo
//        Patient savedPatient = patientRepository.save(newPatient);
//////        normalEmailService.sendHtmlEmail(patient.getEmail(),patient.getName());
//        return patientMapper.EntityToUser(savedPatient);

        //3
//        UserEntity managedUser = (UserEntity) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//
//        // 1. Check karo ki is USER ka patient record hai ya nahi (Not by Patient ID)
//        if(patientRepository.existsByUserEntityId(managedUser.getId())){
//            throw new RuntimeException("Patient already exists for this user");
//        }
//
//        // 2. DTO to Entity
//        Patient newPatient = patientMapper.userToEntity(patientPostRequestDTO);
//        System.out.println("DEBUG: Patient ID before save: " + newPatient.getId());
//
//        // 3. Sabse Important: ID ko null rakho taaki DB auto-generate kare
//        newPatient.setId(null);
//        newPatient.setUserEntity(managedUser);
//
//        // 4. Save
//        Patient savedPatient = patientRepository.save(newPatient);
//
//        return patientMapper.EntityToUser(savedPatient);
    }


    //getPatientPageById
//    public PatientPageResponseDTO getPatientPageById(Integer id){
//            PatientPageResponseDTO patient = patientRepository.findPatientWithPageById(id);
//            return modelMapper
//                    .map(patient,PatientPageResponseDTO.class);
//    }

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
