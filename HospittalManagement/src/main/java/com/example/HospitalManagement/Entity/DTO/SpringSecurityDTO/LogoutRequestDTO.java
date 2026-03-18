package com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequestDTO {

    @Valid
    private String refreshToken;
}
