package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-12T11:04:33+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Microsoft)"
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
        departmentNoIDResponseDTO.setHeadDoctorName( departmentProjectionDTO.getHeadDoctorName() );

        return departmentNoIDResponseDTO;
    }

    @Override
    public DoctorResponseDTO EntityTOUser(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();

        doctorResponseDTO.setDepartmentNames( departmentSetToStringSet( doctor.getDepartments() ) );
        doctorResponseDTO.setId( doctor.getId() );
        doctorResponseDTO.setName( doctor.getName() );
        doctorResponseDTO.setSpecialization( doctor.getSpecialization() );
        doctorResponseDTO.setEmail( doctor.getEmail() );

        return doctorResponseDTO;
    }

    protected Set<String> departmentSetToStringSet(Set<Department> set) {
        if ( set == null ) {
            return null;
        }

        Set<String> set1 = new LinkedHashSet<String>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Department department : set ) {
            set1.add( mapDepartmentToString( department ) );
        }

        return set1;
    }
}
