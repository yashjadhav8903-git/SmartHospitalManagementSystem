package com.example.HospitalManagement.Entity.EntityType;

import com.example.HospitalManagement.Entity.PermissionEntity;
import com.example.HospitalManagement.Entity.RoleEntity;
import com.example.HospitalManagement.Enums.RolesType;
import com.example.HospitalManagement.OAuth2Google.AuthProviderType;
import com.example.HospitalManagement.SpringSecurity.RolePermissionMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString(exclude = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "app_user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(unique = true,nullable = false)
    private String username;

    private String password;
    private String providerId;

    private String name;

    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;



    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)  //--> should be retrieved from the database immediately when the parent entity is loaded.
    @Enumerated(EnumType.STRING)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();


    /**
     * when you store role and permission direct DB
     */
    @Override
    @JsonIgnore
    /// ***** Important bat --> Role bhi authority hai or Permission bhi authority hai
    // bs Bas prefix difference hai --> 1. ROLE_ADMIN   2. Appointment:Write

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();  //--> Empty authority list create

        for (RoleEntity role : roles) { // role pe loop
            // 1. Add Role Authority (e.g., "ROLE_ADMIN").
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRolesName().name()));

            // 2. Add Permission Authorities directly from DB.
            for (PermissionEntity permission : role.getPermissions()) { // permission pr bhi loop
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionName().getPermissions())); // e.g., "Patient:Read"[cite: 9]
            }
        }
        return authorities;
    }


    // when you hardcore value best for  practice
//    @Override
//    @net.minidev.json.annotate.JsonIgnore
//    public Collection<? extends GrantedAuthority> getAuthorities() { // GrantedAuthority is Interface. Ye Spring ka core method hai, Jab user authenticate hota hai,Spring yahi method call karta hai.

//        /// ***** Important baat --> Role bhi authority hai or Permission bhi authority hai
//        /// bs Bas prefix difference hai --> 1. ROLE_ADMIN   2. Appointment:Write
//
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();   //--> Empty authority list create
//        roles.forEach(       // --> User ke roles loop kar rahe ho.
//                roles -> {
//                    Set<SimpleGrantedAuthority> permissions = RolePermissionMapping.getAuthoritieforRole(roles); //--> Each role ke permissions fetch ho rahe.
//                    authorities.addAll(permissions);  //--> Permissions add ho rahe main authority set me
//                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roles.name()));  //--> Yaha role ko bhi authority bana diya.
//                }
//        );
//        return authorities;
//    }



    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserEntity that = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


}
// --> User Entity for username/password kaha store hoga ,login ke time verify kisse karenge