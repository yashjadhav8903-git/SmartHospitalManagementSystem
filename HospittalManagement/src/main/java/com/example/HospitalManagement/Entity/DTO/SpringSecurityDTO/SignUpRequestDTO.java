package com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO;

import com.example.HospitalManagement.Enums.RolesType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {

    private String username;
    private String password;
    private String name;

    private Set<RolesType> roles = new HashSet<>();  //--> only for understanding aise role define nahi krna . admine khud role assign krega .
}
