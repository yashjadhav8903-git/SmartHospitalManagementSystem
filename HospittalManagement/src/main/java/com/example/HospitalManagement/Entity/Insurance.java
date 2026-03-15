package com.example.HospitalManagement.Entity;

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
    public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true,length = 50)
    private String policyNumber;

    @Column(nullable = false,length = 100)
    private String provider;

    @Column(nullable = false)
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "insurance")  // mapped with insurance field       //--> Inverse Side
    private Patient patient;
}
//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .
///--> Ager Aap Mapped nahi krte toh ek join table create hoti hai. isliye inverse side pr mapping karna.