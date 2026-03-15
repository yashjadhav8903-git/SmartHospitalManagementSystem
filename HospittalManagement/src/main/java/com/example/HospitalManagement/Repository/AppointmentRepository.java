package com.example.HospitalManagement.Repository;
import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.Projection.ForAppointmens.AppointmentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // --> AutoAppointmentSet
     boolean existsByDoctorIdAndAppointmentTimeAndStatusIn  (
             Integer doctorId,
             LocalDateTime appointmentTime,
             List<AppointmentStatus> statuses
     );




    @Query(value = "select * from Patient", nativeQuery = true)
    List<Patient> findAllPatient();

    @Query(value = "select * from appointment",nativeQuery = true)
    List<Appointment> showAllAppointments();

    @Query("""
            select a from Appointment a
            join a.patient p
            where p.id = :patientId""")
    List<Appointment> finaAppointments(@Param("patientId") int patientId);


    //---> getAppointmentWithProjection
    @Query( """
                select a.id as appointmentId,
                a.appointmentTime as appointmentTime,
                a.reason as appointmentReason,
                p.name as patientName,
                d.name as doctorName
                from Appointment a
                join a.patient p
                join a.doctor d
                where d.id =:doctorId
            """)
    Page<AppointmentProjection> findAppointmentWithPage(@Param("doctorId") Integer doctorId,Pageable pageable);

    //--->GetAppointmentById
    @Query("""
           select a.id as appointmentId,
                a.appointmentTime as appointmentTime,
                a.reason as appointmentReason,
                p.name as patientName,
                d.name as doctorName
                from Appointment a
                join a.patient p
                join a.doctor d
            where p.id = :patientId
           """)
    Page<AppointmentProjection> findAppointmentByPatientId(@Param("patientId") Integer patientId, Pageable pageable);
}