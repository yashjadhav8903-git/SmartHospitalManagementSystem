package com.example.HospitalManagement.DTO.PatientsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRegisterResponseDTO {

    private Integer id;
    private String username;
    private String accessToken;
    private String refreshToken;
    private String text;

}
