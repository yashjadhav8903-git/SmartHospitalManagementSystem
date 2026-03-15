package com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Sign up ( ye controller me aate hai ) --> DB me save hone ke baad response
public class SignUpResponseDTO {
    private Integer id;
    private String username;
}
