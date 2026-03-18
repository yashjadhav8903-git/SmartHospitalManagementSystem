package com.example.HospitalManagement.RefreshTokenConfg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRedisDTO implements Serializable {

    private Integer id;
    private String token;
    private Instant expiredDate;
    private String username;

}
// --> Reason why create this RefreshTokenRedisDTO ?
//--> : Redis ko expiredDate chahiye taaki hum validation kar sakein. Agar tum LoginResponseDTO Redis mein save karoge, toh usme expiry date miss ho jayegi. isliye create This DTO.