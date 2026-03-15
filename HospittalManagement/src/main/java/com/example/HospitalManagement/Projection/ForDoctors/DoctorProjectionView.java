package com.example.HospitalManagement.Projection.ForDoctors;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DoctorProjectionView {

    Integer getId();
    String getName();
    String getSpecialization();
    String getEmail();

    Set<DepartmentProjectionView> getDepartments();
}
