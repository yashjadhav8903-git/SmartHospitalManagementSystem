package com.example.HospitalManagement.AddressInfo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String addressLine;
    private String state;
    private String pincode;
    private String city;
}
