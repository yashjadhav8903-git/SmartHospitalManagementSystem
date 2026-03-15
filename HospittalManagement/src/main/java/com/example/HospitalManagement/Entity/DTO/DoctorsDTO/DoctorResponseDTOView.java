package com.example.HospitalManagement.Entity.DTO.DoctorsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTOView {

    private Integer id;
    private String name;
    private String specialization;
    private String email;
    Set<DepartmentResponseDTOView> DoctorInDepartment;
}
