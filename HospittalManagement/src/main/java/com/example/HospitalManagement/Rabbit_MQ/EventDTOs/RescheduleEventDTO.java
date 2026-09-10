package com.example.HospitalManagement.Rabbit_MQ.EventDTOs;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * PAYLOAD class (Data Package)
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer appointmentId;
    private String email;
    private String patientName;
    private String doctorName;
    private LocalDate newDate;
    private String startTime;
    private String endTime;
}
