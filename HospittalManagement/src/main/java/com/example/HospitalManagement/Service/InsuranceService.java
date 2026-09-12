package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.DTO.InsurancesDTO.InsurancePlanResponseDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.InsurancePlan;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.PatientInsurance;
import com.example.HospitalManagement.Enums.InsuranceStatus;
import com.example.HospitalManagement.ExceptionHandling.InsuranceExpiredException;
import com.example.HospitalManagement.ExceptionHandling.InsuranceNotFoundException;
import com.example.HospitalManagement.ExceptionHandling.NoActiveInsurancePolicyFoundException;
import com.example.HospitalManagement.ExceptionHandling.PatientNotFoundException;
import com.example.HospitalManagement.MapStruct.InsuranceMapper;
import com.example.HospitalManagement.Repository.InsurancePlanRepository;
import com.example.HospitalManagement.Repository.PatientInsuranceRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final PatientRepository patientRepository;
    private final InsuranceMapper insuranceMapper;
    private final InsurancePlanRepository insurancePlanRepository;
    private final PatientInsuranceRepository patientInsuranceRepository;

    @Transactional
    @AuditLog(action = "ASSIGN_INSURANCE", resource = "Insurance")
    @PreAuthorize("hasAuthority('Insurance:Operations')")
    public AssignInsurancePatientResponseDTO AssignInsurancePatient(AssignInsuranceToPatientsRequestDTO dto,
                                                                      Integer patientId){
        Patient patient = patientRepository.findById(patientId).orElseThrow(()->
                new PatientNotFoundException("Patient not found at this Id : " + patientId));

        // --> request to Entity
        InsurancePlan insurancePlan = insurancePlanRepository.findById(dto.getInsurancePlanId())
                        .orElseThrow(() -> new InsuranceNotFoundException("Insurance Plan not found with ID: " + dto.getInsurancePlanId()));

        // create insurance
        PatientInsurance patientInsurance = PatientInsurance.builder()
                .insurancePlan(insurancePlan)
                .policyNumber(dto.getPolicyNumber())
                .startDate(LocalDate.now())
                .validUntil(LocalDate.now().plusMonths(insurancePlan.getValidateInMonths()))
                .patient(patient)
                .insuranceStatus(InsuranceStatus.ACTIVE)
                .build();

        // save in database
        PatientInsurance saved = patientInsuranceRepository.save(patientInsurance);
        // Entity --> User
        return insuranceMapper.EntityToResponse(saved);
    }

    @Transactional
    @AuditLog(action = "CANCEL_INSURANCE", resource = "Insurance")
    @PreAuthorize("hasAuthority('Insurance:Read') or @patientInsurance.isInsuranceOwner(#patientId, authentication.name)")
    public void cancelInsuranceForPatient(Integer patientId){

        log.info("Cancelling insurance policy for patient ID {}", patientId);

        PatientInsurance patientInsurance = patientInsuranceRepository.findByPatientId(patientId)
                .orElseThrow(()->
                        new PatientNotFoundException("Patient Not Found at this Id :" + patientId));

        if(patientInsurance.getInsuranceStatus() != InsuranceStatus.ACTIVE) {
            throw new NoActiveInsurancePolicyFoundException("No active insurance policy found to cancel for patient ID: " + patientId);
        }

        patientInsurance.setInsuranceStatus(InsuranceStatus.CANCELLED);
        patientInsuranceRepository.save(patientInsurance);

        log.info("Insurance policy {} marked as CANCELLED for patient ID {}",
                patientInsurance.getPolicyNumber(), patientId);

    }


    @Transactional
    @AuditLog(action = "RE-ACTIVE_INSURANCE",resource = "Insurance")
    @PreAuthorize("hasAuthority('Insurance:Read') or @patientInsurance.isInsuranceOwner(#patientId, authentication.name)")
    public AssignInsurancePatientResponseDTO reActiveInsurance(Integer patientId){

        log.info("Re-active insurance policy for patient ID {}", patientId);

        if(!patientRepository.existsById(patientId)){
            throw new PatientNotFoundException("Patient Not Found at this Id :" + patientId);
        }

        PatientInsurance patientInsurance = patientInsuranceRepository.findByPatientId(patientId)
                .orElseThrow(()->
                        new InsuranceNotFoundException("No previous insurance record found to reactivate for patient ID: " + patientId));


        if(patientInsurance.getValidUntil().isBefore(LocalDate.now())) {
            patientInsurance.setInsuranceStatus(InsuranceStatus.EXPIRED);
            patientInsuranceRepository.save(patientInsurance);
            throw new InsuranceExpiredException("Cannot reactivate policy. Policy validity has expired!");
        }

        // re-Active
        patientInsurance.setInsuranceStatus(InsuranceStatus.ACTIVE);
        patientInsuranceRepository.save(patientInsurance);

        return insuranceMapper.EntityToResponse(patientInsurance);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<InsurancePlanResponseDTO> getInsurancePlans(){

        log.info("Fetching all active insurance plans catalog");
        return insurancePlanRepository.findAll()
                .stream()
                .map(this::toInsurancePlan)
                .toList();
    }



    private InsurancePlanResponseDTO toInsurancePlan(InsurancePlan insurancePlan){

        InsurancePlanResponseDTO insurancePlanDTO = new InsurancePlanResponseDTO();

        insurancePlanDTO.setId(insurancePlan.getId());
        insurancePlanDTO.setInsuranceType(insurancePlan.getInsuranceType());
        insurancePlanDTO.setPlanName(insurancePlan.getPlanName());
        insurancePlanDTO.setProvider(insurancePlan.getProvider());
        insurancePlanDTO.setValidateInMonths(insurancePlan.getValidateInMonths());

        return insurancePlanDTO;
    }

}