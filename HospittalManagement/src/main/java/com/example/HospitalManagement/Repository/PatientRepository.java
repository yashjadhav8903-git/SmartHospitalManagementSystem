package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {


    // PatientRepository.java
    boolean existsByUserEntityId(Integer userId);

    //--> Pagination
    @NotNull Page<Patient> findAll(@NotNull Pageable pageable);

    Optional<Patient> findByUserEntityId(Integer userEntityId);

    Page<Patient>findById(Integer id,Pageable pageable);




    // // -->GetAllPatientWithInsuranceWithMapstruct
    @Query("""
            select p.id as id,
            p.name as name,
            p.email as email,
            i.insurancePlan.provider as provider,
            i.insurancePlan.insuranceType as insuranceType,
            i.validUntil as validUntil,
            i.createdAt as createdAt
            from Patient p
            left join p.insurance i
            """)
    Page<PatientInsuranceProjection> getAllPatientWithInsurance(Pageable pageable);


}
