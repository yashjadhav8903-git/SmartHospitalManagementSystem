package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.RolesType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Set<RoleEntity> findAllByRolesNameIn(Set<RolesType> rolesNames);
    Optional<RoleEntity> findByRolesName(RolesType rolesName);
}
