package com.example.HospitalManagement.Entity.DTO.DepartmentsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDeptDTO {

    private Integer id;
    private String departmentNames;
    private String headDoctorName;

    // kyu ki hum me Department me doctors chahiye ( doctor form in that department )
    Page<DoctorResponseDeptDTO> doctors;
}
