package com.example.HospitalManagement.Projection.ForDepartments;

import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentProjectionDTO {

    Integer getId();
    String getName();
    String getHeadDoctorName();
}
