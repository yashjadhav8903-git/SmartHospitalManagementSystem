package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.DoctorsDTO.DepartmentResponseDTOView;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.DoctorResponseDTOView;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.ExceptionHandling.DoctorException;
import com.example.HospitalManagement.ExceptionHandling.DoctorNotFoundException;
import com.example.HospitalManagement.Projection.ForDoctors.DoctorProjectionView;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.RoleRepository;
import com.example.HospitalManagement.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {
    private final RoleRepository roleRepository;

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;




    // --> projection + Pageable + byDoctorById
    @Transactional
    @Cacheable(cacheNames = "doctors" , key = "#id + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @PreAuthorize("hasAuthority('Doctor:Read') and #id == authentication.principal.id")
    @AuditLog(action = "get_Doctor:Id", resource = "Doctor")
    public Page<DoctorResponseDTOView> getDoctorsById(Integer id , Pageable pageable){
        log.info("Fetching Doctor with ID: {}",id);
        System.out.println("🔥 DATABASE HIT - Fetching Doctor from DB" + id);

        Page<DoctorProjectionView> page = doctorRepository.findById(id,pageable);
        return page .map( doctor -> new DoctorResponseDTOView(
                    doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getEmail(),
                doctor.getDepartments()
                        .stream()
                        .map(dept -> new DepartmentResponseDTOView(
                        dept.getDepartmentNames()))
                        .collect(Collectors.toSet())
        ));
    }


    // --> All Doctor
    @Transactional
    @PreAuthorize("hasAuthority('Doctor:Read')")
    public Page<DoctorResponseDTOView> getAllDoctors(Pageable pageable){

        Page<Doctor> page1 = doctorRepository.findAll(pageable);
        return page1.map(DoctorProjectionView -> new DoctorResponseDTOView(
                    DoctorProjectionView.getId(),
                DoctorProjectionView.getName(),
                DoctorProjectionView.getSpecialization(),
                DoctorProjectionView.getEmail(),
                DoctorProjectionView.getDepartments()
                        .stream().map(DepartmentResponseDTOView -> new DepartmentResponseDTOView(
                                DepartmentResponseDTOView.getName())
        ).collect(Collectors.toSet())));
    }


    // --> Create Doctor / add Doctor to Database
    @Transactional
    @CacheEvict(cacheNames = "doctors",allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(action = "Doctor:OnBoard" ,resource = "Doctor")
    public DoctorPOSTResponseDTO onBoardDoctor(DoctorPOSTRequestDTO doctorPOSTRequestDTO){

        log.info("Onboarding new doctor for User ID: {}", doctorPOSTRequestDTO.getUserId());

        // 1. Check UserEntity exists
        UserEntity userEntity = userRepository.findById(doctorPOSTRequestDTO.getUserId())
                .orElseThrow(() -> new DoctorNotFoundException("User not found with ID: " + doctorPOSTRequestDTO.getUserId()));


        // 2. Check if Doctor profile already exists for this User
        if(doctorRepository.existsByUserEntityId(doctorPOSTRequestDTO.getUserId())){
            throw new DoctorException("Doctor Already exist at this ID " + doctorPOSTRequestDTO.getUserId());
        }


        // 3. Fetch DOCTOR Role
        RoleEntity doctorRole = roleRepository.findByRolesName(RolesType.DOCTOR)
                .orElseThrow(() -> new DoctorNotFoundException("Role DOCTOR not found in DB"));

        // 4. Safely check if role already present by Name/ID
        boolean hasDoctorRole = userEntity.getRoles().stream()
                .anyMatch(role -> role.getRolesName().equals(RolesType.DOCTOR));


        /// Assign DOCTOR role to UserEntity -> Assign role ONLY IF missing (Fixes 409 Duplicate Key for userId: 1)
        if(!hasDoctorRole){
            userEntity.getRoles().add(doctorRole);
            userRepository.save(userEntity);
        }


        // 5. Build & Save Doctor Entity
        Doctor doctor = Doctor.builder()
                .name(doctorPOSTRequestDTO.getName())
                .specialization(doctorPOSTRequestDTO.getSpecialization())
                .email(doctorPOSTRequestDTO.getEmail())
                .userEntity(userEntity)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return modelMapper.map(savedDoctor, DoctorPOSTResponseDTO.class);
    }

}
