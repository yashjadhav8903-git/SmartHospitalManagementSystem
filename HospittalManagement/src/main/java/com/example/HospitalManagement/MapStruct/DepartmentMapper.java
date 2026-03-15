package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Department;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    //1 --> DepartmentProjection to DepartmentNOIDResponseDTO
    DepartmentNoIDResponseDTO ProjectionToDTO(DepartmentProjectionDTO departmentProjectionDTO);

    //2--> Request To Entity
    @Mapping(source = "departmentNames",target = "departmentNames")
    Department UserToEntity(AssignDepartmentRequestDTO assignDepartmentRequestDTO);

    //3 --> Entity to Response
    DoctorResponseDTO EntityTOUser(Doctor doctor);
}
