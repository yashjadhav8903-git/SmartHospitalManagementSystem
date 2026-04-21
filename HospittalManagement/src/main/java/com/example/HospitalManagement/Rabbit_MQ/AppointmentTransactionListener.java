package com.example.HospitalManagement.Rabbit_MQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// ---> Listener class
@Component
@RequiredArgsConstructor
public class AppointmentTransactionListener {

    private final RabbitTemplate rabbitTemplate;

    // Phase: AFTER_COMMIT matlab DB mein data pakka save hone ke baad hi chalega
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterEvent(AppointmentBookEvent event){
        rabbitTemplate.convertAndSend(
                "booking_exchange",
                "booking.confirmed",
                event.getBookingEventDTO()
        );
    }

}
