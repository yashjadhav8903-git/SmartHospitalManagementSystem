package com.example.HospitalManagement.Entity.DTO.DoctorsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPOSTResponseDTO {

    private Integer userId;
    private String name;
    private  String specialization;
    private String email;
}
