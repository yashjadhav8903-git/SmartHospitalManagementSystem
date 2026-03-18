package com.example.HospitalManagement.Entity.DTO.DoctorsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponseDTOView implements Serializable {

    private Integer id;
    private String name;
    private String specialization;
    private String email;
    Set<DepartmentResponseDTOView> DoctorInDepartment;
}
