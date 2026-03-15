package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.Entity.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.Insurance;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.MapStruct.InsuranceMapper;
import com.example.HospitalManagement.Repository.InsuranceRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final PatientRepository patientRepository;
    private final InsuranceMapper insuranceMapper;
    private final ModelMapper modelMapper;

    @Transactional
    @PreAuthorize("hasAuthority('Insurance:Operations')")
    public AssignInsurancePatientResponseDTO AssignInsurancePatient(AssignInsuranceToPatientsRequestDTO dto,
                                                                      Integer patientId){
        Patient patient = patientRepository.findById(patientId).orElseThrow(()->
                new EntityNotFoundException("Patient not found at this Id : " + patientId));

        // --> request to Entity
        Insurance insurance = insuranceMapper.toInsuranceEntity(dto);

        patient.setInsurance(insurance);  //--> Patient ko insurance set karna
        insurance.setPatient(patient);    //--> Same as insurance to patient set karna. kyu ki bidirectional consistency maintain

        // save in database
        Insurance saved = insuranceRepository.save(insurance);
        // Entity --> User
        return insuranceMapper.EntityToResponse(saved);
    }

    @Transactional
    @PreAuthorize("hasAuthority('Insurance:Operations')")
    //Remove only Patient-Insurance Not a Patient
    public Patient RemoveInsuranceFromPatient(Integer patientId){
        Patient patient = patientRepository.findById(patientId).orElseThrow(()->
                new EntityNotFoundException("Patient Not Found at this Id :" + patientId));

        patient.setInsurance(null);
        return patient;
    }
}