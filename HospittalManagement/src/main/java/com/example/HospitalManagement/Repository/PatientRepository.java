package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {


    // PatientRepository.java
    boolean existsByUserEntityId(Integer userId);

    boolean existsByIdAndUserEntityUsername(Integer id, String username);

    //--> Pagination
    @NotNull Page<Patient> findAll(@NotNull Pageable pageable);

    Optional<Patient> findByUserEntityId(Integer userEntityId);

    Page<Patient>findById(Integer id,Pageable pageable);




    // // -->GetAllPatientWithInsuranceWithMapstructs
    @Query("""

            select p.id as id,
                        p.name as name,
                        p.email as email,
                        pi.id as insuranceId,
                        ins.provider as provider,
                        ins.insuranceType as insuranceType,
                        pi.validUntil as validUntil,
                        pi.createdAt as createdAt
                        from Patient p
                        left join PatientInsurance pi on p.id = pi.patient.id
                        left join pi.insurancePlan ins
            """)
    Page<PatientInsuranceProjection> getAllPatientWithInsurance(Pageable pageable);


    @Query("""
            select p.id as id,
            p.name as name,
            p.email as email,
            pi.id as insuranceId,
            ins.provider as provider,
            ins.insuranceType as insuranceType,
            pi.validUntil as validUntil,
            pi.createdAt as createdAt
            from Patient p
            left join PatientInsurance pi on p.id = pi.patient.id
            left join pi.insurancePlan ins
            where p.id = :patientId
            """)
    Optional<PatientInsuranceProjection> getPatientInsuranceById(@Param("patientId") Integer patientId);


}
