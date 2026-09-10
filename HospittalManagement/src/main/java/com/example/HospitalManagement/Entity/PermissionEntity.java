package com.example.HospitalManagement.Entity;

import com.example.HospitalManagement.Enums.PermissionsType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true,nullable = false)
    private PermissionsType permissionName;  // PATIENT_READ, APPOINTMENT_WRITE
}
