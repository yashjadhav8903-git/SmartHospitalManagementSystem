package com.example.HospitalManagement.DTO.PatientsDTO;

import com.example.HospitalManagement.Enums.Blood_Group_type;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSignUpRequestDTO {

    // Auth & Basic Info
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 30)
    private String name;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Patient Profile Metadata
    @NotBlank(message = "Gender is required")
    private String gender;

    private LocalDate birthdate;

    @JsonProperty("bloodGroup")
    private Blood_Group_type bloodGroup;

    // Address Details
    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "PinCode is required")
    private String pincode;

    private String addressLine;
}
