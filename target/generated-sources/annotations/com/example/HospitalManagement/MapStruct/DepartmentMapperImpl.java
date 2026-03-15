package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Department;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T19:38:31+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class DepartmentMapperImpl implements DepartmentMapper {

    @Override
    public DepartmentNoIDResponseDTO ProjectionToDTO(DepartmentProjectionDTO departmentProjectionDTO) {
        if ( departmentProjectionDTO == null ) {
            return null;
        }

        DepartmentNoIDResponseDTO departmentNoIDResponseDTO = new DepartmentNoIDResponseDTO();

        departmentNoIDResponseDTO.setId( departmentProjectionDTO.getId() );
        departmentNoIDResponseDTO.setDepartmentNames( departmentProjectionDTO.getDepartmentNames() );
        departmentNoIDResponseDTO.setHeadDoctorName( departmentProjectionDTO.getHeadDoctorName() );

        return departmentNoIDResponseDTO;
    }

    @Override
    public Department UserToEntity(AssignDepartmentRequestDTO assignDepartmentRequestDTO) {
        if ( assignDepartmentRequestDTO == null ) {
            return null;
        }

        Department department = new Department();

        department.setDepartmentNames( assignDepartmentRequestDTO.getDepartmentNames() );

        return department;
    }

    @Override
    public DoctorResponseDTO EntityTOUser(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();

        doctorResponseDTO.setId( doctor.getId() );
        doctorResponseDTO.setName( doctor.getName() );
        doctorResponseDTO.setSpecialization( doctor.getSpecialization() );
        doctorResponseDTO.setEmail( doctor.getEmail() );

        return doctorResponseDTO;
    }
}
