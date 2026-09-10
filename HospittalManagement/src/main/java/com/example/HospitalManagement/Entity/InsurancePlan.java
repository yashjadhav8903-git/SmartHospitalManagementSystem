package com.example.HospitalManagement.Entity;

import com.example.HospitalManagement.Enums.InsuranceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "insurance_plans")
public class InsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String provider;

    @Column(nullable = false,length = 100)
    private String planName;


    private Integer validateInMonths;   // e.g. 12 months ya 1 year's


    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

}
