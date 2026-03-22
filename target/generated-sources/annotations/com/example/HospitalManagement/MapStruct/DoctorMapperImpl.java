package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DoctorResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.DoctorPOSTResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-22T14:32:10+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorResponseDeptDTO DoctorToDTO(DoctorProjectionDTO doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorResponseDeptDTO doctorResponseDeptDTO = new DoctorResponseDeptDTO();

        doctorResponseDeptDTO.setId( doctor.getId() );
        doctorResponseDeptDTO.setName( doctor.getName() );
        doctorResponseDeptDTO.setSpecialization( doctor.getSpecialization() );
        doctorResponseDeptDTO.setEmail( doctor.getEmail() );

        return doctorResponseDeptDTO;
    }

    @Override
    public DepartmentResponseDeptDTO DeptToDTO(DepartmentProjectionDTO dept) {
        if ( dept == null ) {
            return null;
        }

        DepartmentResponseDeptDTO departmentResponseDeptDTO = new DepartmentResponseDeptDTO();

        departmentResponseDeptDTO.setId( dept.getId() );
        departmentResponseDeptDTO.setDepartmentNames( dept.getDepartmentNames() );
        departmentResponseDeptDTO.setHeadDoctorName( dept.getHeadDoctorName() );

        return departmentResponseDeptDTO;
    }

    @Override
    public Doctor UserToEntity(DoctorPOSTRequestDTO doctorPOSTRequestDTO) {
        if ( doctorPOSTRequestDTO == null ) {
            return null;
        }

        Doctor.DoctorBuilder doctor = Doctor.builder();

        doctor.name( doctorPOSTRequestDTO.getName() );
        doctor.specialization( doctorPOSTRequestDTO.getSpecialization() );
        doctor.email( doctorPOSTRequestDTO.getEmail() );

        return doctor.build();
    }

    @Override
    public DoctorPOSTResponseDTO EntityToUser(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorPOSTResponseDTO doctorPOSTResponseDTO = new DoctorPOSTResponseDTO();

        doctorPOSTResponseDTO.setName( doctor.getName() );
        doctorPOSTResponseDTO.setSpecialization( doctor.getSpecialization() );
        doctorPOSTResponseDTO.setEmail( doctor.getEmail() );

        return doctorPOSTResponseDTO;
    }
}
