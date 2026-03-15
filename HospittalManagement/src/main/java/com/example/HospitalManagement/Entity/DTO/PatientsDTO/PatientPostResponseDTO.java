package com.example.HospitalManagement.Entity.DTO.PatientsDTO;

import com.example.HospitalManagement.Entity.EntityType.Blood_Group_type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientPostResponseDTO {

    private String name;
    private String email;
    private String gender;
    private LocalDate birthdate;
    private Blood_Group_type BloodGroup;
}
