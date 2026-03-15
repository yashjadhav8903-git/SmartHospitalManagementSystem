package com.example.HospitalManagement.Projection.ForPatients;

import com.example.HospitalManagement.Enums.InsuranceType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface PatientInsuranceProjection {

    Integer getId();
    String getName();
    String getEmail();

    String getProvider();
    InsuranceType getInsuranceType();
    LocalDate  getValidUntil();
    LocalDateTime getCreatedAt();

}
