package com.example.HospitalManagement.DTO.DepartmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDeptDTO {

    private Integer id;
    private String name;
    private String specialization;
    private String email;

}
