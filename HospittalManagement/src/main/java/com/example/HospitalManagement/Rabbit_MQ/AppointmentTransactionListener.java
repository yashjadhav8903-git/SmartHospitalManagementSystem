package com.example.HospitalManagement.Rabbit_MQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 🟢 PRODUCER CLASS(Message Bhejney Wala) -> Producer (Publishes to RabbitMQ)
 */

// ---> Listener class
@Component
@RequiredArgsConstructor
public class AppointmentTransactionListener {

    private final RabbitTemplate rabbitTemplate;

    // for confiremed
    // Phase: AFTER_COMMIT matlab DB mein data pakka save hone ke baad hi chalega
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterEvent(AppointmentBookEvent event){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CONFIRMED,
                event.getBookingEventDTO()
        );
    }


    // for cancelled
    // Phase: AFTER_COMMIT matlab DB mein data pakka save hone ke baad hi chalega
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAftercancelEvent(BookingCancelEvent bookingCancelEvent){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_CANCELLED,
                bookingCancelEvent.getCancelEventDTO()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRescheduledEvent(AppointmentRescheduleEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESCHEDULED,
                event.getRescheduleEventDTO()
        );
    }
}
