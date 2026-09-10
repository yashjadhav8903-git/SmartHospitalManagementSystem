package com.example.HospitalManagement.DTO.AppointmentsDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RescheduleRequestDTO {

    private Integer appointmentId;
    private Integer newSlotId;

}
