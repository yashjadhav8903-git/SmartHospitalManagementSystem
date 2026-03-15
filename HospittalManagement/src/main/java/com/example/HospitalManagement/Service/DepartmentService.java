package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DoctorResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Department;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.MapStruct.DepartmentMapper;
import com.example.HospitalManagement.MapStruct.DoctorMapper;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import com.example.HospitalManagement.Repository.DepartmentRepository;
import com.example.HospitalManagement.Repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {


    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final DoctorMapper doctorMapper;
    private final DepartmentMapper departmentMapper;


    //-->Assign Doctor to Department
    @Transactional
    @PreAuthorize("hasAuthority('Department:Assign')")
    public DoctorResponseDTO assignDoctor(Integer doctorId, AssignDepartmentRequestDTO assignDepartmentRequestDTO){

        // 1. get existing doctors
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(()
                -> new RuntimeException("Doctor Not Found at this DoctorId" + doctorId));

        // 2 . get or createDepartments
        Department department = departmentRepository.findByDepartmentNames(assignDepartmentRequestDTO.getDepartmentNames())
                .orElseGet(() -> CreateDepartment(assignDepartmentRequestDTO.getDepartmentNames()));

        // 3 . Mapping
        doctor.getDepartments().add(department);

        // 4 . Save
        Doctor saved = doctorRepository.save(doctor);
        return departmentMapper.EntityTOUser(doctor);
    }
    private Department CreateDepartment(String name){
        Department department = new Department();
        department.setDepartmentNames(name);
        return departmentRepository.save(department);
    }




    //getDepartmentAndDoctorById
    @Transactional
    @PreAuthorize("hasAuthority('Department:Operations') and #id == authentication.principal.id")
    public DepartmentResponseDeptDTO getDepartmentAndDoctorById (Integer id , Pageable pageable){

        // 1. fetch by Department
        DepartmentProjectionDTO dept =
                departmentRepository.findDepartmentById(id);

        if(dept == null){
             throw new RuntimeException("Department Not Found ?? ");
        }

        // 2 .fetch doctor from department
        Page<DoctorProjectionDTO> doctorpage =
                departmentRepository.findDoctorByDepartmentId(id,pageable);

        if (doctorpage.isEmpty()) {
            throw new RuntimeException("No Doctors In That Department");
        }

        // 3 . Department Mapping using Mapstruct
        DepartmentResponseDeptDTO dto = doctorMapper.DeptToDTO(dept);

        // 4 . Doctor Mapping using Mapstruct
        Page<DoctorResponseDeptDTO> doctorDTOPage = doctorpage.map(doctorMapper::DoctorToDTO);

        dto.setDoctors(doctorDTOPage);
        return dto;


        // mapping
//        DepartmentResponseDeptDTO dto = new DepartmentResponseDeptDTO();
//
//        dto.setId(dept.getId());
//        dto.setDepartmentNames(dept.getDepartmentNames());
//        dto.setHeadDoctorName(dept.getHeadDoctorName());
//
//
//        // Doctor --> DTO
//        Page<DoctorResponseDeptDTO> doctorDTO =
//                doctorpage.map( d ->
//                        new DoctorResponseDeptDTO(
//                                d.getId(),
//                                d.getName(),
//                                d.getSpecialization(),
//                                d.getEmail()
//                        )
//                );
//        dto.setDoctors(doctorDTO);
//        return dto;
    }

        // --> FindAllDepartment
//    public Page<DepartmentProjectionDTO> getAllDepartment(Pageable pageable){
//        return departmentRepository.findAllDepartment(pageable);
//    }


    // --> FindAllDepartment with Mapstruct
    @Transactional
    @PreAuthorize("hasAuthority('Department:Operations')")
    public Page<DepartmentNoIDResponseDTO> getAllDepartment(Pageable pageable) {
        Page<DepartmentProjectionDTO> page =
                departmentRepository.findAllDepartment(pageable);
        return page.map(departmentMapper::ProjectionToDTO);

    //--> without Mapstruct manual Mapping
//        // Projection → DTO mapping
//        return page.map(p -> new DepartmentNoIDResponseDTO(
//                p.getId(),
//                p.getDepartmentNames(),
//                p.getHeadDoctorName()
//        ));
    }


}