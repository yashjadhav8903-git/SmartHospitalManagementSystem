package com.example.HospitalManagement.DTO.PatientsDTO;

import com.example.HospitalManagement.Enums.Blood_Group_type;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientPostRequestDTO {

    @NotBlank(message = "PatientName is Required Field")
    @Size(min = 2 , max= 30 , message = "Name should be of length 2 to 30 Character's")
    private String name;

    @NotBlank(message = "Email is Required Field")
    private String email;

    @NotBlank(message = "Enter gender")
    private String gender;

    private LocalDate birthdate;

}
