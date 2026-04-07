package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DoctorResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.AllPatientDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientInsuranceResponseDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPostRequestDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPostResponseDTO;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import com.example.HospitalManagement.Projection.ForPatients.GetAllPatientProjection;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {


    // ---> Projection to ResponseDTO
    // 1. Projection to PatientInsuranceResponseDTO ( source is Projection and target is ResponseDTO )
    @Mapping(source = "id",target = "patientId")
    @Mapping(source = "name",target = "patientName")
    @Mapping(source = "email",target = "patientEmail")
    // 2 . Projection to InsuranceResponseDTO ( source is Projection and target is ResponseDTO )
    @Mapping(source = "provider",target = "insurance.provider")
    @Mapping(source = "validUntil",target = "insurance.validUntil")
    @Mapping(source = "insuranceType",target = "insurance.insuranceType")
    @Mapping(source = "createdAt",target = "insurance.createAt")
    PatientInsuranceResponseDTO toDTO(PatientInsuranceProjection p);


    // --> Post Mapping
    //--> user to entity
//    @Mapping(target = "userEntity", ignore = true)
    Patient userToEnity(PatientPostRequestDTO patientPostRequestDTO);

    //---> Entity to User
    @Mapping(source = "bloodGroup",target = "bloodGroup")
    PatientPostResponseDTO EntityToUser(Patient patient);

    Patient userToEntity(PatientPostRequestDTO patientPostRequestDTO);

    // PatientEntity to DTO
    AllPatientDTO EntitytoDTO(Patient patient);
    // DTO To PatientEntity
    Patient DTOtoEntity(AllPatientDTO allPatientDTO);

}
