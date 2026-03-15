package com.example.HospitalManagement.Entity.EntityType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.print.Doc;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "department_names",nullable = false,unique = true,length = 100)
    private String departmentNames;


    @OneToOne
    private Doctor headDoctor; //--> Owning side


    @ManyToMany(mappedBy = "departments")
    private Set<Doctor> doctors = new HashSet<>(); //--> inverse side
}
//*** IMPORTANT ***//
///-->  Ager Aap Inverse field me jar kr kuch change kro ge tho vo Own ing side me Nahi hoga . kyu ki foreign key Owning side pr hai .