package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.InsurancePlan;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.PatientInsurance;
import com.example.HospitalManagement.Enums.InsuranceType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-09T13:34:54+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12 (Microsoft)"
)
@Component
public class InsuranceMapperImpl implements InsuranceMapper {

    @Override
    public PatientInsurance toInsuranceEntity(AssignInsuranceToPatientsRequestDTO assignInsuranceToPatients) {
        if ( assignInsuranceToPatients == null ) {
            return null;
        }

        PatientInsurance.PatientInsuranceBuilder patientInsurance = PatientInsurance.builder();

        patientInsurance.policyNumber( assignInsuranceToPatients.getPolicyNumber() );

        return patientInsurance.build();
    }

    @Override
    public AssignInsurancePatientResponseDTO EntityToResponse(PatientInsurance insurance) {
        if ( insurance == null ) {
            return null;
        }

        AssignInsurancePatientResponseDTO assignInsurancePatientResponseDTO = new AssignInsurancePatientResponseDTO();

        assignInsurancePatientResponseDTO.setPatientId( insurancePatientId( insurance ) );
        assignInsurancePatientResponseDTO.setPatientName( insurancePatientName( insurance ) );
        assignInsurancePatientResponseDTO.setProviderName( insuranceInsurancePlanProvider( insurance ) );
        assignInsurancePatientResponseDTO.setInsuranceType( insuranceInsurancePlanInsuranceType( insurance ) );
        assignInsurancePatientResponseDTO.setPolicyNumber( insurance.getPolicyNumber() );
        assignInsurancePatientResponseDTO.setValidUntil( insurance.getValidUntil() );

        return assignInsurancePatientResponseDTO;
    }

    private Integer insurancePatientId(PatientInsurance patientInsurance) {
        if ( patientInsurance == null ) {
            return null;
        }
        Patient patient = patientInsurance.getPatient();
        if ( patient == null ) {
            return null;
        }
        Integer id = patient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String insurancePatientName(PatientInsurance patientInsurance) {
        if ( patientInsurance == null ) {
            return null;
        }
        Patient patient = patientInsurance.getPatient();
        if ( patient == null ) {
            return null;
        }
        String name = patient.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String insuranceInsurancePlanProvider(PatientInsurance patientInsurance) {
        if ( patientInsurance == null ) {
            return null;
        }
        InsurancePlan insurancePlan = patientInsurance.getInsurancePlan();
        if ( insurancePlan == null ) {
            return null;
        }
        String provider = insurancePlan.getProvider();
        if ( provider == null ) {
            return null;
        }
        return provider;
    }

    private InsuranceType insuranceInsurancePlanInsuranceType(PatientInsurance patientInsurance) {
        if ( patientInsurance == null ) {
            return null;
        }
        InsurancePlan insurancePlan = patientInsurance.getInsurancePlan();
        if ( insurancePlan == null ) {
            return null;
        }
        InsuranceType insuranceType = insurancePlan.getInsuranceType();
        if ( insuranceType == null ) {
            return null;
        }
        return insuranceType;
    }
}
