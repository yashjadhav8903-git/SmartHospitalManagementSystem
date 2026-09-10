package com.example.HospitalManagement.Rabbit_MQ;

import com.example.HospitalManagement.Rabbit_MQ.EventDTOs.BookingEventDTO;
import org.springframework.context.ApplicationEvent;

// --> Event Class

public class AppointmentBookEvent extends ApplicationEvent {

    private final BookingEventDTO bookingEventDTO;

    public AppointmentBookEvent(Object source, BookingEventDTO bookingEventDTO) {
        super(source);
        this.bookingEventDTO = bookingEventDTO;
    }
    public BookingEventDTO getBookingEventDTO(){
        return bookingEventDTO;
    }
}
