package com.example.HospitalManagement.Entity.DTO;

import com.example.HospitalManagement.Entity.EntityType.Blood_Group_type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BloodGroupCountResponseEntity {


    private Blood_Group_type Blood_Group_type;
    private Long COUNT;
}
