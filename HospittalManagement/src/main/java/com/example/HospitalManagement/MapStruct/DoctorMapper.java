package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DoctorResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface DoctorMapper {

    //1. --> Doctor projection(Entity) to DoctorResponseDTO (Get)
    DoctorResponseDeptDTO DoctorToDTO(DoctorProjectionDTO doctor);

    // 2. --> department projection(Entity) to DepartmentResponseDTO (get)
    DepartmentResponseDeptDTO DeptToDTO(DepartmentProjectionDTO dept);


    // 3. --> DoctorRequest To Entity ( post )
    Doctor UserToEntity(DoctorPOSTRequestDTO doctorPOSTRequestDTO);

    //4. --> Entity TO Response
    DoctorPOSTResponseDTO EntityToUser(Doctor doctor);
}
