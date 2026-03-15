package com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTO {

    private Integer id;
    private String name;
    private String specialization;
    private String email;
    private Set<AssignDepartmentRequestDTO> DepartmentNames;


}
