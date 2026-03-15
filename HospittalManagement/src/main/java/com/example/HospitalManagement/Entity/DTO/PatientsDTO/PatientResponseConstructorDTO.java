package com.example.HospitalManagement.Entity.DTO.PatientsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseConstructorDTO {  //Projection Constructor

    private Integer id;
    private String name;
    private String gender;
    private String email;
    private Long appointmentCount;

}
