package com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignDepartmentRequestDTO {

    @JsonProperty("departmentName")
    private String DepartmentNames;

}
