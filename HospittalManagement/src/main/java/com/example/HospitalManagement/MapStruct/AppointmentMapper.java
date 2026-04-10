package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Projection.ForAppointmens.AppointmentProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {



    // 1--> User RequestDTO to Entity(Database) ( postMapping )
    @Mapping(source = "appointmentTime",target = "appointmentTime")
    @Mapping(source = "appointmentReason",target = "reason")
    @Mapping(target = "slot",ignore = true)
    @Mapping(target = "id",ignore = true)
    Appointment UserToEntity(CreateAppointmentRequestDTO dto);

    // 2--> Entity to ResponseDTO
    @Mapping(source = "id",target = "appointmentId")
    @Mapping(source = "appointmentTime",target = "appointmentTime")
    @Mapping(source = "reason",target = "reason")
    @Mapping(source = "slot.id",target = "slot")
    @Mapping(source = "patient.name",target = "patientName")
    @Mapping(source = "doctor.name",target = "doctorName")
    @Mapping(source = "status", target = "appointmentStatus")
    CreateAppointmentResponseDTO EntityToDTO(Appointment appointment);


    @Mapping(source = "id",target = "appointmentId")
    @Mapping(source = "appointmentTime",target = "appointmentTime")
    @Mapping(source = "reason",target = "reason")
    @Mapping(source = "slot.id",target = "slot")
    @Mapping(source = "patient.name",target = "patientName")
    @Mapping(source = "doctor.name",target = "doctorName")
    AppointmentResponseDTO EntityPagetoDTO(Appointment appointment);



    // --> Projection to DTO(iska koi bhi role nahi hai. kyuki data post kr rahe hai naki get)
    @Mapping(source = "appointmentId",target = "appointmentId")
    @Mapping(source = "appointmentTime",target = "appointmentTime")
    @Mapping(source = "appointmentReason",target = "reason")
    @Mapping(source = "patientName",target = "patientName")
    @Mapping(source = "doctorName",target = "doctorName")
    @Mapping(source = "appointmentStatus",target = "appointmentStatus")
    CreateAppointmentResponseDTO ProjectionToDTO(AppointmentProjection projection);


    // --> AppointmentProjection to AppointmentResponse(Get)
    @Mapping(source = "appointmentReason",target = "reason")
    AppointmentResponseDTO ProjectionToResSTO(AppointmentProjection appointmentProjection);



    // --> reAssign Appointment ( Entity ---> Response )
    @Mapping(source = "id",target = "appointmentId")
    @Mapping(source = "appointmentTime",target = "appointmentTime")
    @Mapping(source = "patient.name",target = "patientName")
    @Mapping(source = "doctor.name",target = "doctorName")
    @Mapping(source = "doctor.id",target = "doctorId")
    AppointmentReAssignResponseDTO EntityToUser(Appointment appointment);


    // ---> CancelAppointment ( Entity To response )
    @Mapping(source = "id",target = "appointmentId")
    @Mapping(source = "patient.name",target = "patientName")
    @Mapping(source = "doctor.name",target = "doctorName")
    @Mapping(source = "reason",target = "cancelReason")
    @Mapping(source = "status", target = "appointmentStatus")
    CancelAppointmentResponseDTO EntityToResponse(Appointment appointment);

}
