package com.example.HospitalManagement.Projection.ForPatients;

import com.example.HospitalManagement.Entity.EntityType.Blood_Group_type;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface GetAllPatientProjection {

     Integer getId() ;
     String getName();
     String getEmail();
     LocalDate getBirthdate();
     String getGender();
     LocalDateTime getCreatedAt();
     Blood_Group_type getBloodGroup();
}
