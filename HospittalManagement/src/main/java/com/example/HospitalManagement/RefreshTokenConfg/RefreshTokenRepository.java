package com.example.HospitalManagement.RefreshTokenConfg;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<Object> findByToken(String token);
    void deleteByToken(String token);
}