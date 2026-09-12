package com.example.HospitalManagement.DTO.DoctorsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPOSTResponseDTO {

    private Integer DoctorId;
    private String name;
    private  String specialization;
    private String email;
}
