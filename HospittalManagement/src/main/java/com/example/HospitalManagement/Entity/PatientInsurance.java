package com.example.HospitalManagement.Entity;

import com.example.HospitalManagement.Enums.InsuranceStatus;
import com.example.HospitalManagement.Enums.InsuranceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "patient_insurances")
    public class PatientInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true,length = 50)
    private String policyNumber;

    private LocalDate startDate;


    @Column(nullable = false)
    private LocalDate validUntil;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)

    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_insurance_plan_id",nullable = false)
    private InsurancePlan  insurancePlan;

    @Enumerated(EnumType.STRING)
    private InsuranceStatus insuranceStatus;


    @OneToOne  // mapped with insurance field       //--> Inverse Side
    @JoinColumn(name = "patient_id",nullable = false)
    private Patient patient;
}
//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .
///--> Ager Aap Mapped nahi krte toh ek join table create hoti hai. isliye inverse side pr mapping karna.