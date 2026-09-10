package com.example.HospitalManagement.CommandLineAdder;

import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Entity.PermissionEntity;
import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.PermissionsType;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.Repository.PermissionRepository;
import com.example.HospitalManagement.Repository.RoleRepository;
import com.example.HospitalManagement.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;


    @Override
    public void run(String... args) throws Exception {

// Helper Method to fetch or create PermissionEntity
        // 1. ADMIN Permissions
        Set<PermissionEntity> adminPermissions = getPermissionEntities(
                PermissionsType.APPOINTMENT_WRITE,
                PermissionsType.APPOINTMENT_DELETE,
                PermissionsType.APPOINTMENT_READ,
                PermissionsType.PATIENT_READ,
                PermissionsType.PATIENT_DELETE,
                PermissionsType.PATIENT_WRITE,
                PermissionsType.DOCTOR_DELETE,
                PermissionsType.DOCTOR_WRITE,
                PermissionsType.DOCTOR_READ,
                PermissionsType.DOCTOR_ASSIGN,
                PermissionsType.INSURANCE_OPEARTIONS,
                PermissionsType.INSURANCE_READ,
                PermissionsType.USER_MANAGE,
                PermissionsType.REPORT_VIEW,
                PermissionsType.DEPARTMENT_OPERATIONS
        );

        // 2. DOCTOR Permissions
        Set<PermissionEntity> doctorPermissions = getPermissionEntities(
                PermissionsType.DOCTOR_READ,
                PermissionsType.REPORT_VIEW,
                PermissionsType.APPOINTMENT_READ
        );

        // 3. PATIENT Permissions
        Set<PermissionEntity> patientPermissions = getPermissionEntities(
                PermissionsType.DOCTOR_READ,
                PermissionsType.INSURANCE_OPEARTIONS,
                PermissionsType.INSURANCE_READ,
                PermissionsType.APPOINTMENT_READ,
                PermissionsType.APPOINTMENT_WRITE,
                PermissionsType.APPOINTMENT_DELETE
        );

        // --- Role Creation / Update ---

        // ADMIN Role
        RoleEntity adminRole = roleRepository.findByRolesName(RolesType.ADMIN)
                .map(role -> {
                    role.setPermissions(adminPermissions);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .rolesName(RolesType.ADMIN)
                                .permissions(adminPermissions)
                                .build()
                ));

        // DOCTOR Role
        roleRepository.findByRolesName(RolesType.DOCTOR)
                .map(role -> {
                    role.setPermissions(doctorPermissions);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .rolesName(RolesType.DOCTOR)
                                .permissions(doctorPermissions)
                                .build()
                ));

        // PATIENT Role
        roleRepository.findByRolesName(RolesType.PATIENT)
                .map(role -> {
                    role.setPermissions(patientPermissions);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .rolesName(RolesType.PATIENT)
                                .permissions(patientPermissions)
                                .build()
                ));

        // 4. Create Admin User
        String adminEmail = "admin@hospital.com";
        if (!userRepository.existsByUsername(adminEmail)) {

            UserEntity adminUser = UserEntity.builder()
                    .username(adminEmail)
                    .name("Master Admin")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .providerType(AuthProviderType.EMAIL)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);
            log.info("🔥 Default MASTER ADMIN user created with permissions! Email: {}", adminEmail);
        } else {
            log.info("✅ ADMIN user already exists in DB. Skipping initialization.");
        }

    }

    // Reusable Helper to Convert Enums to Set<PermissionEntity>
    private Set<PermissionEntity> getPermissionEntities(PermissionsType... permissions) {
        return Stream.of(permissions)
                .map(type -> permissionRepository.findByPermissionName(type)
                        .orElseGet(() -> permissionRepository.save(
                                PermissionEntity.builder().permissionName(type).build()
                        ))
                ).collect(Collectors.toSet());
    }
}
