package com.example.HospitalManagement.Projection.ForPatients;

import org.springframework.stereotype.Repository;

@Repository
public interface PatientSummaryPage {

    Integer getId();
    String getName();
    String getEmail();
    String getGender();
    Long getAppointmentCount();
}
