package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    //1 --> DepartmentProjection to DepartmentNO-IDResponseDTO
    DepartmentNoIDResponseDTO ProjectionToDTO(DepartmentProjectionDTO departmentProjectionDTO);

    //3 --> Entity to Response
    @Mapping(target = "departmentNames", source = "departments")
    DoctorResponseDTO EntityTOUser(Doctor doctor);


    default String mapDepartmentToString(Department department) {
        return department != null ? department.getName() : null;
    }
}
