package com.example.HospitalManagement.RefreshTokenConfg;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;
    private Instant expiredDate;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity userEntity;

}
