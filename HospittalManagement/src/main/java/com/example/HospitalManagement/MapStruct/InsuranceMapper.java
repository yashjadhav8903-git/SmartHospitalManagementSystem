package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.InsurancesDTO.AssignInsuranceToPatientsRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AssignInsurancePatientResponseDTO;
import com.example.HospitalManagement.Entity.Insurance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InsuranceMapper {

    // --> Request to Entity

    @Mapping(source = "providerName",target = "provider")
    Insurance toInsuranceEntity(AssignInsuranceToPatientsRequestDTO assignInsuranceToPatients);

    // --> Entity to Response
    @Mapping(source = "patient.id",target = "patientId")
    @Mapping(source = "patient.name",target = "patientName")
    AssignInsurancePatientResponseDTO EntityToResponse(Insurance insurance);
}
