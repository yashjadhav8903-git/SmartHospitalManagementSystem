package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.Insurance;
import com.example.HospitalManagement.Entity.Patient;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-16T21:13:16+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class InsuranceMapperImpl implements InsuranceMapper {

    @Override
    public Insurance toInsuranceEntity(AssignInsuranceToPatientsRequestDTO assignInsuranceToPatients) {
        if ( assignInsuranceToPatients == null ) {
            return null;
        }

        Insurance.InsuranceBuilder insurance = Insurance.builder();

        insurance.provider( assignInsuranceToPatients.getProviderName() );
        insurance.policyNumber( assignInsuranceToPatients.getPolicyNumber() );
        insurance.validUntil( assignInsuranceToPatients.getValidUntil() );
        insurance.insuranceType( assignInsuranceToPatients.getInsuranceType() );

        return insurance.build();
    }

    @Override
    public AssignInsurancePatientResponseDTO EntityToResponse(Insurance insurance) {
        if ( insurance == null ) {
            return null;
        }

        AssignInsurancePatientResponseDTO assignInsurancePatientResponseDTO = new AssignInsurancePatientResponseDTO();

        assignInsurancePatientResponseDTO.setPatientId( insurancePatientId( insurance ) );
        assignInsurancePatientResponseDTO.setPatientName( insurancePatientName( insurance ) );
        assignInsurancePatientResponseDTO.setPolicyNumber( insurance.getPolicyNumber() );
        assignInsurancePatientResponseDTO.setValidUntil( insurance.getValidUntil() );
        assignInsurancePatientResponseDTO.setInsuranceType( insurance.getInsuranceType() );

        return assignInsurancePatientResponseDTO;
    }

    private Integer insurancePatientId(Insurance insurance) {
        if ( insurance == null ) {
            return null;
        }
        Patient patient = insurance.getPatient();
        if ( patient == null ) {
            return null;
        }
        Integer id = patient.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String insurancePatientName(Insurance insurance) {
        if ( insurance == null ) {
            return null;
        }
        Patient patient = insurance.getPatient();
        if ( patient == null ) {
            return null;
        }
        String name = patient.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
