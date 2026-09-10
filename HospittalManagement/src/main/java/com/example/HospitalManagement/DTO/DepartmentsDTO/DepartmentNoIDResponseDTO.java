package com.example.HospitalManagement.DTO.DepartmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentNoIDResponseDTO {

    private Integer id;
    private String departmentNames;
    private String headDoctorName;
}
