package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<UserEntity, Integer> {
    // 2( service ke ander )
    Optional<UserEntity> findByUsername(String username);

    UserEntity save(UserEntity builder);

    Optional<UserEntity> findByProviderIdAndProviderType(String providerId, AuthProviderType authProviderType);

    UserEntity findById(Integer userId);
}
