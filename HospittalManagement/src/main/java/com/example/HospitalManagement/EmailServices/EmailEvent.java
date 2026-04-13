//package com.example.HospitalManagement.EmailServices;
//
//import com.example.HospitalManagement.Entity.EntityType.Appointment;
//import com.example.HospitalManagement.Entity.EntityType.Doctor;
//import com.example.HospitalManagement.Entity.Patient;
//import com.example.HospitalManagement.Enums.AppointmentStatus;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//
//@Service
//@RequiredArgsConstructor
//public class EmailEvent {
//
//    private final NormalEmailService normalEmailService;
//
//    public void sendAppointmentEmail(Appointment saved, Patient patient, Doctor doctor) throws MessagingException {
////                 Email ka Logic
//                if(saved.getStatus() == AppointmentStatus.CONFIRMED){
//                    System.out.println("Status is CONFIRMED, sending email...");
//                    normalEmailService.SendConfirmedEmail(
//                            patient.getEmail(),   // 1. Email
//                            patient.getName(),    // 2. Patient ka naam
//                            doctor.getName(),     // 3. Doctor ka naam
//                            saved.getId(),
//                            saved.getSlot().getId(),// 4 .ID
//                            saved.getAppointmentTime().toString(), // 5. Time
//                            saved.getReason()     // 6. Bimari/Reason
//                    );
//                } else if(saved.getStatus() == AppointmentStatus.PENDING) {
//                    System.out.println("Status is PENDING, sending email...");
//                    normalEmailService.SendPendingEmail(
//                            patient.getEmail(),
//                            patient.getName(),
//                            saved.getId(),
//                            saved.getReason()
//                    );
//                }
//    }
//
//
//    public void SendCancelEmail(Appointment appointment, Patient patient, Doctor doctor){
//        try {
//            System.out.println("Status is CANCELLED, sending email...");
//            normalEmailService.CancelledAppointment(
//                    patient.getEmail(),
//                    patient.getName(),
//                    doctor.getName(),
//                    appointment.getId(),
//                    appointment.getSlot().getId(),
//                    appointment.getAppointmentTime().toString(),
//                    appointment.getReason(),
//                    doctor.getId()
//            );
//        } catch (Exception e){
//            System.err.println("Error sending cancellation email: " + e.getMessage());
//        }
//    }
//}
