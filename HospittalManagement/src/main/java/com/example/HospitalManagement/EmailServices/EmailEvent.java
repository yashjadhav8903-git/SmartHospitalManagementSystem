package com.example.HospitalManagement.EmailServices;

import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.Rabbit_MQ.BookingEventDTO;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailEvent {

    private final NormalEmailService normalEmailService;

    public void SendCancelEmail(Appointment appointment, Patient patient, Doctor doctor){
        try {
            System.out.println("Status is CANCELLED, sending email...");
            normalEmailService.CancelledAppointment(
                    patient.getEmail(),
                    patient.getName(),
                    doctor.getName(),
                    appointment.getId(),
                    appointment.getSlot().getId(),
                    appointment.getAppointmentTime().toString(),
                    appointment.getReason(),
                    doctor.getId()
            );
        } catch (Exception e){
            System.err.println("Error sending cancellation email: " + e.getMessage());
        }
    }



    @RabbitListener(queues = "email_queue")
    public void sendAppointmentEmail(BookingEventDTO eventDTO)  {

        try {
            System.out.println("Message received from queue 🔥");
            normalEmailService.SendConfirmedEmail(
                    eventDTO.getEmail(),
                    eventDTO.getUsername(),
                    eventDTO.getDocterName(),
                    eventDTO.getId(),
                    eventDTO.getSlot(),
                    eventDTO.getAppointmentTime().toString(),
                    eventDTO.getReason()
            );
        }
        catch (Exception e){
            System.err.println("Error sending Appointment-Booking email: " + e.getMessage());
        }
    }
}
