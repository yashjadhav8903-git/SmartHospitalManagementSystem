package com.example.HospitalManagement.Entity.EntityType;

import com.example.HospitalManagement.Enums.PermissionType;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.SpringSecurity.RolePermissionMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "app_user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(unique = true,nullable = false)
    private String username;

    private String password;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;



    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)  //--> should be retrieved from the database immediately when the parent entity is loaded.
    @Enumerated(EnumType.STRING)
    Set<RolesType> roles = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // GrantedAuthority is Interface. Ye Spring ka core method hai, Jab user authenticate hota hai,Spring yahi method call karta hai.
//        return roles.stream()
//                .map(rolesType -> new SimpleGrantedAuthority("ROLE_" + rolesType.name())) // --> SimpleGrantedAuthority is Implementation of GrantedAuthority.
//                .collect(Collectors.toSet());



        /// ***** Important baat --> Role bhi authority hai or Permission bhi authority hai
        /// bs Bas prefix difference hai --> 1. ROLE_ADMIN   2. Appointment:Write

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();   //--> Empty authority list create
        roles.forEach(       // --> User ke roles loop kar rahe ho.
                roles -> {
                    Set<SimpleGrantedAuthority> permissions = RolePermissionMapping.getAuthoritieforRole(roles); //--> Each role ke permissions fetch ho rahe.
                    authorities.addAll(permissions);  //--> Permissions add ho rahe main authority set me
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roles.name()));  //--> Yaha role ko bhi authority bana diya.
                }
        );
        return authorities;
    }
}
// --> User Entity for username/password kaha store hoga ,login ke time verify kisse karenge