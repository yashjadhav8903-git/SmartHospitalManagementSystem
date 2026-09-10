package com.example.HospitalManagement.Rabbit_MQ;

import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.RescheduleEventDTO;
import org.springframework.context.ApplicationEvent;

/**
 * Ye sirf Spring Framework ka internal wrapper event hai jo AppointmentService se data le kar AppointmentTransactionListener
 * tak safely pahunchata hai (DB Transaction Commit hone ke baad).
 */

public class AppointmentRescheduleEvent extends ApplicationEvent {

    private final RescheduleEventDTO rescheduleEventDTO;

    public AppointmentRescheduleEvent(Object source, RescheduleEventDTO rescheduleEventDTO) {
        super(source);
        this.rescheduleEventDTO = rescheduleEventDTO;
    }

    public RescheduleEventDTO getRescheduleEventDTO() {
        return this.rescheduleEventDTO;
    }
}
