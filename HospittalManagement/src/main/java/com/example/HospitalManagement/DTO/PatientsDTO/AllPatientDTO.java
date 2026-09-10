package com.example.HospitalManagement.DTO.PatientsDTO;

import com.example.HospitalManagement.Enums.Blood_Group_type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllPatientDTO implements Serializable {

    private Integer id ;
    private String name;
    private String email;
    private LocalDate birthdate;
    private String gender;
    private LocalDateTime createdAt;
    private Blood_Group_type BloodGroup;
}
