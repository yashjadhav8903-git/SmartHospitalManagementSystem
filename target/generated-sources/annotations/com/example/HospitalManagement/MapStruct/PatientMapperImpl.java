package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.InsurancesDTO.InsuranceResponseDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AllPatientDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientInsuranceResponseDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPostRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPostResponseDTO;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-07T20:34:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Override
    public PatientInsuranceResponseDTO toDTO(PatientInsuranceProjection p) {
        if ( p == null ) {
            return null;
        }

        PatientInsuranceResponseDTO patientInsuranceResponseDTO = new PatientInsuranceResponseDTO();

        patientInsuranceResponseDTO.setInsurance( patientInsuranceProjectionToInsuranceResponseDTO( p ) );
        patientInsuranceResponseDTO.setPatientId( p.getId() );
        patientInsuranceResponseDTO.setPatientName( p.getName() );
        patientInsuranceResponseDTO.setPatientEmail( p.getEmail() );

        return patientInsuranceResponseDTO;
    }

    @Override
    public Patient userToEnity(PatientPostRequestDTO patientPostRequestDTO) {
        if ( patientPostRequestDTO == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.name( patientPostRequestDTO.getName() );
        patient.email( patientPostRequestDTO.getEmail() );
        patient.birthdate( patientPostRequestDTO.getBirthdate() );
        patient.gender( patientPostRequestDTO.getGender() );

        return patient.build();
    }

    @Override
    public PatientPostResponseDTO EntityToUser(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientPostResponseDTO patientPostResponseDTO = new PatientPostResponseDTO();

        patientPostResponseDTO.setBloodGroup( patient.getBloodGroup() );
        patientPostResponseDTO.setName( patient.getName() );
        patientPostResponseDTO.setEmail( patient.getEmail() );
        patientPostResponseDTO.setGender( patient.getGender() );
        patientPostResponseDTO.setBirthdate( patient.getBirthdate() );

        return patientPostResponseDTO;
    }

    @Override
    public Patient userToEntity(PatientPostRequestDTO patientPostRequestDTO) {
        if ( patientPostRequestDTO == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.name( patientPostRequestDTO.getName() );
        patient.email( patientPostRequestDTO.getEmail() );
        patient.birthdate( patientPostRequestDTO.getBirthdate() );
        patient.gender( patientPostRequestDTO.getGender() );

        return patient.build();
    }

    @Override
    public AllPatientDTO EntitytoDTO(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        AllPatientDTO allPatientDTO = new AllPatientDTO();

        allPatientDTO.setId( patient.getId() );
        allPatientDTO.setName( patient.getName() );
        allPatientDTO.setEmail( patient.getEmail() );
        allPatientDTO.setBirthdate( patient.getBirthdate() );
        allPatientDTO.setGender( patient.getGender() );
        allPatientDTO.setCreatedAt( patient.getCreatedAt() );
        allPatientDTO.setBloodGroup( patient.getBloodGroup() );

        return allPatientDTO;
    }

    @Override
    public Patient DTOtoEntity(AllPatientDTO allPatientDTO) {
        if ( allPatientDTO == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.id( allPatientDTO.getId() );
        patient.name( allPatientDTO.getName() );
        patient.email( allPatientDTO.getEmail() );
        patient.birthdate( allPatientDTO.getBirthdate() );
        patient.gender( allPatientDTO.getGender() );
        patient.createdAt( allPatientDTO.getCreatedAt() );

        return patient.build();
    }

    protected InsuranceResponseDTO patientInsuranceProjectionToInsuranceResponseDTO(PatientInsuranceProjection patientInsuranceProjection) {
        if ( patientInsuranceProjection == null ) {
            return null;
        }

        InsuranceResponseDTO insuranceResponseDTO = new InsuranceResponseDTO();

        insuranceResponseDTO.setProvider( patientInsuranceProjection.getProvider() );
        insuranceResponseDTO.setValidUntil( patientInsuranceProjection.getValidUntil() );
        insuranceResponseDTO.setInsuranceType( patientInsuranceProjection.getInsuranceType() );
        insuranceResponseDTO.setCreateAt( patientInsuranceProjection.getCreatedAt() );

        return insuranceResponseDTO;
    }
}
