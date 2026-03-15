package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Repository.AppointmentRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Service.AppointmentService;
import com.example.HospitalManagement.Service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class AppointmentTest {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private AppointmentRepository appointmentRepository;
//
//    @Test
//    public void TestCreateAppointment() throws IllegalAccessException {
//        Appointment appointment = Appointment.builder()
//                .appointmentTime(LocalDateTime.of(2025,1,20,14,20,10))
//                .reason("Brain Operation")
//                .build();
//        Appointment Newappointment = appointmentService.CreateNewAppointment(appointment, 1, 3);
//        System.out.println(Newappointment);
//
//        //--->reAssignAppointment
//        Appointment Updatedappointment = appointmentService.reAssignAppointmentTOAnotherDoctor(Newappointment.getId(), 2);
//        System.out.println(Updatedappointment);
//
//    }
//    @Test
//    public void TestCreateAppointment1() throws IllegalAccessException {
//        Appointment appointment1 = Appointment.builder().appointmentTime(LocalDateTime.of(2024,12,16,1,48,12))
//                .reason("Eye's Problem")
//                .build();
//        Appointment NewAppointment1 = appointmentService.CreateNewAppointment(appointment1,4,3);
//        System.out.println(NewAppointment1);
//
//    }

    @Test
    //RemoveAppointment
    public void RemoveAppointment(){
        Appointment NewRemoveAppointment = appointmentService.RemoveAppointment(42,10);
        System.out.println(NewRemoveAppointment);
    }
//
//    @Test
//    public void RemoveBulkOfAppointments(){
//        Patient patient = patientRepository.findById(2).orElseThrow(() ->
//                new RuntimeException("No Patient Found"));
//
//        List<Integer> isRemove = List.of(16,17);
//
//        appointmentService.RemoveBulkOfAppointments(patient.getId(), isRemove);
//
//
////        Patient UpdatePatient = patientRepository.findById(patient.getId()).get();
////        List<Integer>RemainAppointment = UpdatePatient.getAppointments()
////                .stream()
////                .map(Appointment::getId)
////                .toList();
//    }
//    @Test
//    public void AllAppointment(){
//        List<Appointment> appointments = appointmentRepository.showAllAppointments();
//        System.out.println(appointments);
//    }
//    @Test
//    public void GetAppointmentById() {
//        List<Appointment> getAppointment = appointmentRepository.finaAppointments(2);
//        System.out.println(getAppointment);
//    }
}
