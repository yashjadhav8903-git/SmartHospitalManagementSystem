package com.example.HospitalManagement.Entity;

import com.example.HospitalManagement.Entity.EntityType.Appointment;
import com.example.HospitalManagement.Entity.EntityType.Blood_Group_type;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    @Column(nullable = false,length = 100)
    private String name;

//    @Column(unique = true,nullable = false)
    private String email;

//    @Column(nullable = false)
    private LocalDate birthdate;

//    @Column(nullable = false,length = 25)
    private String gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @JsonProperty("BloodGroup")
    private Blood_Group_type BloodGroup;

    @OneToOne
//    @MapsId ///--> where the child shares the same Primary Key as the parent.(patient_id and userEntity_id are same both use that ,there own perpective or usecase)
    // Optional login account
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;


    // Admin who created patient
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;


    @OneToOne (cascade = {CascadeType.REMOVE,CascadeType.MERGE,CascadeType.PERSIST},orphanRemoval = true)                    //--> Owning Side
    private Insurance insurance; ///--> Patient and Insurance Connection that it !
                                 ///--> One to One Mapping One Patient have only one Insurance's
    //--> Find Patient by Insurance_Id

    @OneToMany(mappedBy = "patient", cascade = {CascadeType.REMOVE},orphanRemoval = true)  //--> mapped by patient field (Inverse Side)
    @ToString.Exclude
     private List<Appointment> appointments = new ArrayList<>();  ///---> One Patient have Multiple Appointments , that's why List.
}

//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .
///--> Ager Aap Mapped nahi krte toh ek join table create hoti hai. isliye inverse side pr mapping karna.