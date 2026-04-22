package com.example.HospitalManagement.Rabbit_MQ;

import org.springframework.context.ApplicationEvent;

public class BookingCancelEvent extends ApplicationEvent {

    private final CancelEventDTO cancelEventDTO;

    public BookingCancelEvent(Object source, CancelEventDTO cancelEventDTO) {
        super(source);
        this.cancelEventDTO = cancelEventDTO;
    }

    public CancelEventDTO getCancelEventDTO(){
        return this.cancelEventDTO;
    }
}
