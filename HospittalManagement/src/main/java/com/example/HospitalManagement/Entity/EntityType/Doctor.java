package com.example.HospitalManagement.Entity.EntityType;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(length = 100)
    private String specialization;

//    @Column(nullable = false,unique = true,length = 100)
    private String email;

    @OneToOne
    @MapsId ///--> where the child shares the same Primary Key as the parent.(Doctor_id and userEntity_id are same both use that ,there own perpective or usecase)
    private UserEntity userEntity;


    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Department> departments = new HashSet<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

}
//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .
///--> Ager Aap Mapped nahi krte toh ek join table create hoti hai. isliye inverse side pr mapping karna.