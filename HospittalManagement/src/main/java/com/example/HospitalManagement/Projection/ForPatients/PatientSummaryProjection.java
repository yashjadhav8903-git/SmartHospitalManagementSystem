package com.example.HospitalManagement.Projection.ForPatients;

import org.springframework.stereotype.Repository;

@Repository
public interface PatientSummaryProjection {  //Interface Projection

    Integer getId();
    String getName();
    String getGender();
    String getEmail();
    Long getAppointmentCount();


}
