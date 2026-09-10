package com.example.HospitalManagement.Rabbit_MQ;

import com.example.HospitalManagement.EmailServices.NormalEmailService;
import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.RescheduleEventDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * CONSUMER CLASS (Message Padhne Wala)  -> Consumer (Listens to RabbitMQ)
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEmailConsumer {

    private final NormalEmailService normalEmailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESCHEDULED)
    public void consumeRescheduleEvent(RescheduleEventDTO rescheduleEventDTO) {
        log.info("Received Reschedule Event from RabbitMQ for Appointment ID: {}", rescheduleEventDTO.getAppointmentId());

        normalEmailService.SendRescheduledEmail(
                rescheduleEventDTO.getEmail(),
                rescheduleEventDTO.getPatientName(),
                rescheduleEventDTO.getDoctorName(),
                rescheduleEventDTO.getAppointmentId(),
                rescheduleEventDTO.getNewDate(),
                rescheduleEventDTO.getStartTime(),
                rescheduleEventDTO.getEndTime()
        );
    }
}