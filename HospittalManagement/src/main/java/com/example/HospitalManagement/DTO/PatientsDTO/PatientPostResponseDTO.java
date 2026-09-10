package com.example.HospitalManagement.DTO.PatientsDTO;

import com.example.HospitalManagement.Enums.Blood_Group_type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientPostResponseDTO {

    private Integer id;
    private String name;
    private String email;
    private String gender;
    private LocalDate birthdate;

}
