package com.example.HospitalManagement.Entity.EntityType;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Integer id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private boolean isBooked = false;

    @ManyToOne
    private Doctor doctor;
}
