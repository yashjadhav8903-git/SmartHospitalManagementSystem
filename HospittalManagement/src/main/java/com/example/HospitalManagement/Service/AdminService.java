package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.CustomAnnotations.AuditLog;
import com.example.HospitalManagement.DTO.AdminDTOs.AdminRequestDTOs;
import com.example.HospitalManagement.DTO.AdminDTOs.AdminResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.ExceptionHandling.DuplicateEmailIdResourceException;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.Repository.RoleRepository;
import com.example.HospitalManagement.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @AuditLog(action = "Admin_onBoarding" , resource = "Admin")
    public AdminResponseDTO onboarding(AdminRequestDTOs  adminRequestDTOs, AuthProviderType  authProviderType) {

        log.info("onboarding Request come to AdminService : {}",adminRequestDTOs.getUsername());

        String username = adminRequestDTOs.getUsername().toLowerCase().trim();

        if(userRepository.existsByUsername(username)) {
            throw new DuplicateEmailIdResourceException("Admin with username " + username + " already exists.");
        }


        RoleEntity roleEntity1 = roleRepository.findByRolesName(RolesType.ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("Default ADMIN role not found in DB"));


        // build user
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(adminRequestDTOs.getPassword())
                .providerType(authProviderType)
                .name(username)
                .roles(Set.of(roleEntity1))
                .build();


        if(authProviderType == AuthProviderType.EMAIL && adminRequestDTOs.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(adminRequestDTOs.getPassword()));
        }

        UserEntity save = userRepository.save(userEntity);

        log.info("ADMIN onboarded successfully via direct registration: {}", save.getUsername());

        return new AdminResponseDTO(
                save.getId(),
                save.getUsername()
        );
    }
}
