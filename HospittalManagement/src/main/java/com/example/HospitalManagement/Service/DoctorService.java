package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DepartmentResponseDTOView;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorResponseDTOView;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.MapStruct.DoctorMapper;
import com.example.HospitalManagement.Projection.ForDoctors.DoctorProjectionView;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.SpringSecurity.UserRepository;
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

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    private final DoctorMapper doctorMapper;



    // --> projection + Pageable + byDoctorById
    @Transactional
        @Cacheable(cacheNames = "doctors" , key = "#id + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @PreAuthorize("hasAuthority('Doctor:Read') and #id == authentication.principal.id")
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
                                DepartmentResponseDTOView.getDepartmentNames())
        ).collect(Collectors.toSet())));
    }


    // --> Create Doctor / add Doctor to Database
//    @Transactional
//    public DoctorPOSTResponseDTO CreateNewDoctor(DoctorPOSTRequestDTO doctorPOSTRequestDTO){
//        Doctor NewDoctor = doctorMapper.UserToEntity(doctorPOSTRequestDTO);
//        Doctor doctors = doctorRepository.save(NewDoctor);
//        return doctorMapper.EntityToUser(doctors);
//    }


    // --> Create Doctor / add Doctor to Database
    @Transactional
    @CacheEvict(cacheNames = "doctors",allEntries = true)
    @PreAuthorize("hasAuthority('Doctor:Write')")
    public DoctorPOSTResponseDTO onBoardDooctor(DoctorPOSTRequestDTO doctorPOSTRequestDTO){
        UserEntity userEntity = userRepository.findById(doctorPOSTRequestDTO.getUserId());

        if(doctorRepository.existsById(doctorPOSTRequestDTO.getUserId())){
            throw new IllegalArgumentException("Doctor Already exist");
        }

        Doctor doctor = Doctor.builder()
                .name(doctorPOSTRequestDTO.getName())
                .specialization(doctorPOSTRequestDTO.getSpecialization())
                .userEntity(userEntity)
                .build();

        userEntity.getRoles().add(RolesType.DOCTOR);
        return modelMapper.map(doctorRepository.save(doctor),DoctorPOSTResponseDTO.class);
//        return doctorMapper.EntityToUser(doctor);
    }

}
