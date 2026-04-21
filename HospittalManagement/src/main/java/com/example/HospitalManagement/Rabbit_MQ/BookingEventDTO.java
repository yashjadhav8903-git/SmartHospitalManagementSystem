package com.example.HospitalManagement.Rabbit_MQ;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEventDTO {

    private Integer id;
    private String email;
    private String username;
    private Integer slot;
    private String DocterName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;
    private String reason;
}
