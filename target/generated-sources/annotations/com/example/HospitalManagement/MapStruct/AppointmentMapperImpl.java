package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.AppointmentReAssignResponseDTO;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.AppointmentResponseDTO;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.CancelAppointmentResponseDTO;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.CreateAppointmentRequestDTO;
import com.example.HospitalManagement.Entity.DTO.AppointmentsDTO.CreateAppointmentResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.Projection.ForAppointmens.AppointmentProjection;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-14T19:38:31+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class AppointmentMapperImpl implements AppointmentMapper {

    @Override
    public Appointment UserToEntity(CreateAppointmentRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Appointment.AppointmentBuilder appointment = Appointment.builder();

        appointment.appointmentTime( dto.getAppointmentTime() );
        appointment.reason( dto.getAppointmentReason() );

        return appointment.build();
    }

    @Override
    public CreateAppointmentResponseDTO EntityToDTO(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        CreateAppointmentResponseDTO createAppointmentResponseDTO = new CreateAppointmentResponseDTO();

        createAppointmentResponseDTO.setAppointmentId( appointment.getId() );
        createAppointmentResponseDTO.setAppointmentTime( appointment.getAppointmentTime() );
        createAppointmentResponseDTO.setReason( appointment.getReason() );
        createAppointmentResponseDTO.setPatientName( appointmentPatientName( appointment ) );
        createAppointmentResponseDTO.setDoctorName( appointmentDoctorName( appointment ) );
        createAppointmentResponseDTO.setAppointmentStatus( appointment.getStatus() );

        return createAppointmentResponseDTO;
    }

    @Override
    public CreateAppointmentResponseDTO ProjectionToDTO(AppointmentProjection projection) {
        if ( projection == null ) {
            return null;
        }

        CreateAppointmentResponseDTO createAppointmentResponseDTO = new CreateAppointmentResponseDTO();

        createAppointmentResponseDTO.setAppointmentId( projection.getAppointmentId() );
        createAppointmentResponseDTO.setAppointmentTime( projection.getAppointmentTime() );
        createAppointmentResponseDTO.setReason( projection.getAppointmentReason() );
        createAppointmentResponseDTO.setPatientName( projection.getPatientName() );
        createAppointmentResponseDTO.setDoctorName( projection.getDoctorName() );
        if ( projection.getAppointmentStatus() != null ) {
            createAppointmentResponseDTO.setAppointmentStatus( Enum.valueOf( AppointmentStatus.class, projection.getAppointmentStatus() ) );
        }

        return createAppointmentResponseDTO;
    }

    @Override
    public AppointmentResponseDTO ProjectionToResSTO(AppointmentProjection appointmentProjection) {
        if ( appointmentProjection == null ) {
            return null;
        }

        AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO();

        appointmentResponseDTO.setReason( appointmentProjection.getAppointmentReason() );
        appointmentResponseDTO.setAppointmentId( appointmentProjection.getAppointmentId() );
        appointmentResponseDTO.setAppointmentTime( appointmentProjection.getAppointmentTime() );
        appointmentResponseDTO.setPatientName( appointmentProjection.getPatientName() );
        appointmentResponseDTO.setDoctorName( appointmentProjection.getDoctorName() );

        return appointmentResponseDTO;
    }

    @Override
    public AppointmentReAssignResponseDTO EntityToUser(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        AppointmentReAssignResponseDTO appointmentReAssignResponseDTO = new AppointmentReAssignResponseDTO();

        appointmentReAssignResponseDTO.setAppointmentId( appointment.getId() );
        appointmentReAssignResponseDTO.setAppointmentTime( appointment.getAppointmentTime() );
        appointmentReAssignResponseDTO.setPatientName( appointmentPatientName( appointment ) );
        appointmentReAssignResponseDTO.setDoctorName( appointmentDoctorName( appointment ) );
        appointmentReAssignResponseDTO.setDoctorId( appointmentDoctorId( appointment ) );
        appointmentReAssignResponseDTO.setStatus( appointment.getStatus() );

        return appointmentReAssignResponseDTO;
    }

    @Override
    public CancelAppointmentResponseDTO EntityToResponse(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }

        CancelAppointmentResponseDTO cancelAppointmentResponseDTO = new CancelAppointmentResponseDTO();

        cancelAppointmentResponseDTO.setAppointmentId( appointment.getId() );
        cancelAppointmentResponseDTO.setPatientName( appointmentPatientName( appointment ) );
        cancelAppointmentResponseDTO.setDoctorName( appointmentDoctorName( appointment ) );
        cancelAppointmentResponseDTO.setCancelReason( appointment.getReason() );

        return cancelAppointmentResponseDTO;
    }

    private String appointmentPatientName(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Patient patient = appointment.getPatient();
        if ( patient == null ) {
            return null;
        }
        String name = patient.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String appointmentDoctorName(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Doctor doctor = appointment.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        String name = doctor.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Integer appointmentDoctorId(Appointment appointment) {
        if ( appointment == null ) {
            return null;
        }
        Doctor doctor = appointment.getDoctor();
        if ( doctor == null ) {
            return null;
        }
        Integer id = doctor.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
