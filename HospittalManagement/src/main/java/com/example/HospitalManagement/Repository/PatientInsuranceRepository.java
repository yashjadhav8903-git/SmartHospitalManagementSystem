package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientInsuranceRepository extends JpaRepository<PatientInsurance,Integer> {

    Optional<PatientInsurance> findByPatientId(Integer patientId);

}
