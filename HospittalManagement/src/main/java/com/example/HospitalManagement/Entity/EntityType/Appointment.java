package com.example.HospitalManagement.Entity.EntityType;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime appointmentTime;


    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    private DoctorSlot slot;
    //--> Owning Side
    @ManyToOne
    @JoinColumn(nullable = false)  //Patient is Required
    private Patient patient;

    //--> Owning Side
    @ManyToOne
    @JoinColumn(nullable = false)
    private Doctor doctor;

}
//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .