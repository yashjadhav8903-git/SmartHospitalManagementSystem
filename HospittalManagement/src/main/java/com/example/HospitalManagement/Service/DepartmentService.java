package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.DTO.DepartmentsDTO.DoctorResponseDeptDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.ExceptionHandling.DoctorNotFoundException;
import com.example.HospitalManagement.MapStruct.DepartmentMapper;
import com.example.HospitalManagement.MapStruct.DoctorMapper;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {


    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;
    private final DepartmentMapper departmentMapper;


    //-->Assign Doctor to Department
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(resource = "Department_AssignToDoctor's", action = "ASSIGN_DOCTOR")
    public DoctorResponseDTO assignDoctor(Integer doctorId, AssignDepartmentRequestDTO assignDepartmentRequestDTO){

        log.info("Assigning doctor ID {} to department '{}'", doctorId, assignDepartmentRequestDTO.getDepartmentNames());

        // 1. get existing doctors
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(()
                -> new DoctorNotFoundException("Doctor Not Found at this DoctorId" + doctorId));

        // 2 . get or createDepartments
        Department department = createDepartment(assignDepartmentRequestDTO.getDepartmentNames().trim());

        // 3 . Mapping
        doctor.getDepartments().add(department);

        // 4 . Save
        Doctor doctorSaved = doctorRepository.save(doctor);
        return departmentMapper.EntityTOUser(doctorSaved);
    }


    private Department createDepartment(String name){
        return departmentRepository.findByNameIgnoreCase(name)
                .orElseGet( () -> {
                    try {
                        Department department = Department.builder()
                                .name(name)
                                .build();
                        return departmentRepository.saveAndFlush(department);
                    } catch (DataIntegrityViolationException e) {
                        log.warn("Concurrent creation detected for department '{}', retrying fetch", name);

                        // Handle race condition: If created by another thread concurrently
                        return departmentRepository.findByNameIgnoreCase(name)
                                .orElseThrow(() -> new IllegalStateException("Unable to resolve department: " + name));

                    }
                });
    }



    //getDepartmentAndDoctorById
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Department:Operations')")
    public DepartmentResponseDeptDTO getDepartmentAndDoctorById (Integer id , Pageable pageable) {

        log.debug("Fetching department and doctors for department ID {}", id);

        // 1. fetch by Department
        DepartmentProjectionDTO deptProjection = departmentRepository.findDepartmentProjectionById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));


        // 2 .fetch doctor from department
        Page<DoctorProjectionDTO> doctorpage =
                departmentRepository.findDoctorByDepartmentId(id, pageable);


        // 3 . Department Mapping using MapStruct
        DepartmentResponseDeptDTO responseDto = doctorMapper.DeptToDTO(deptProjection);

        // 4 . Doctor Mapping using MapStruct
        Page<DoctorResponseDeptDTO> doctorDTOPage = doctorpage.map(doctorMapper::DoctorToDTO);

        responseDto.setDoctors(doctorDTOPage);
        return responseDto;

    }


    // --> FindAllDepartment with MapStruct
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Department:Operations')")
    public Page<DepartmentNoIDResponseDTO> getAllDepartment(Pageable pageable) {

        log.debug("Fetching all departments paginated");

        Page<DepartmentProjectionDTO> page = departmentRepository.findAllDepartmentProjections(pageable);
        return page.map(departmentMapper::ProjectionToDTO);

    }
}