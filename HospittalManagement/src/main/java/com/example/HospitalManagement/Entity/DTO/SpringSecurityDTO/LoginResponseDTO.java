package com.example.HospitalManagement.Entity.DTO.SpringSecurityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
// ( ye controller me aate hai )
public class LoginResponseDTO {

    String jwt;
    String refreshToken;
    Integer userId;

}
