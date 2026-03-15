package com.example.HospitalManagement.RefreshTokenConfg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {

    private String refreshToken;
}
