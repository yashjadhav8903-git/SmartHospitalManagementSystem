package com.example.HospitalManagement.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actor;    // kisne kiya
    private String action;  // kya kiya
    private String resource;  // kis pr kiya
    private String ipAddress; // user ka ip address
    private String status; // uska status success or failed

    @Column(columnDefinition = "TEXT")
    private String details;

    private LocalDateTime dateTime;
}
