package com.example.HospitalManagement.Projection.ForDepartments;

import org.springframework.stereotype.Repository;

@Repository

public interface DoctorProjectionDTO {

    Integer getId();
    String getName();
    String getSpecialization();
    String getEmail();

}
