package com.example.HospitalManagement.EmailServices;

import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import com.example.HospitalManagement.Rabbit_MQ.BookingEventDTO;
import com.example.HospitalManagement.Rabbit_MQ.CancelEventDTO;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class EmailEvent {

    private final NormalEmailService normalEmailService;

    @RabbitListener(queues = "cancel_email_queue")
    public void SendCancelEmail(CancelEventDTO cancelEventDTO){
        try {
            System.out.println("Status is CANCELLED, sending email..." + cancelEventDTO.getEmail());
            normalEmailService.CancelledAppointment(
                    cancelEventDTO.getEmail(),
                    cancelEventDTO.getPatientName(),
                    cancelEventDTO.getDocterName(),
                    cancelEventDTO.getSlot(),
                    cancelEventDTO.getDate(),
                    cancelEventDTO.getId(),
                    cancelEventDTO.getAppointmentTime().toString(),
                    cancelEventDTO.getReason(),
                    cancelEventDTO.getId()
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
                    eventDTO.getSlot(),
                    eventDTO.getDate(),
                    eventDTO.getId(),
                    eventDTO.getAppointmentTime().toString(),
                    eventDTO.getReason()
            );
        }
        catch (Exception e){
            System.err.println("Error sending Appointment-Booking email: " + e.getMessage());
        }
    }
}
