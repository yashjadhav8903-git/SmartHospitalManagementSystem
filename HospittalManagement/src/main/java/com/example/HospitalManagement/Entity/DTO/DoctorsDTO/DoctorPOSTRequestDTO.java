package com.example.HospitalManagement.Entity.DTO.DoctorsDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorPOSTRequestDTO {


    private Integer userId;

    @NotBlank(message = "DoctorName is Required Field")
    @Size(min = 3 , max= 30 , message = "Name should be of length 3 to 30 Character's")
    private String name;

    @NotBlank(message = "specialization is Required Field")
    @Size(min = 1,max = 50 , message = "Specialization should be of length 3 to 30 Character's")
    private String specialization;

    @Email
    @NotBlank(message = "Enter Valid Email")
    private String email;
}
