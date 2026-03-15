package com.example.HospitalManagement.Entity.DTO.DepartmentsDTO;

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
