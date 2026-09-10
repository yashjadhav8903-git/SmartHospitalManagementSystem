package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.PermissionEntity;
import com.example.HospitalManagement.Enums.PermissionsType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {

    Optional<PermissionEntity> findByPermissionName(PermissionsType permissionName);
}
