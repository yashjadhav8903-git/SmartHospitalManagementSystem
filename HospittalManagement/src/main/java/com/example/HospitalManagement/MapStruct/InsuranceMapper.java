package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;

import com.example.HospitalManagement.Entity.PatientInsurance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InsuranceMapper {

    // --> Request to Entity

    PatientInsurance toInsuranceEntity(AssignInsuranceToPatientsRequestDTO assignInsuranceToPatients);

    // --> Entity to Response
    @Mapping(source = "patient.id",target = "patientId")
    @Mapping(source = "patient.name",target = "patientName")
    @Mapping(source = "insurancePlan.id",target = "insuranceId")
    @Mapping(source = "insurancePlan.provider",target = "providerName")
    @Mapping(source = "insurancePlan.insuranceType",target = "insuranceType")
    AssignInsurancePatientResponseDTO EntityToResponse(PatientInsurance insurance);
}
