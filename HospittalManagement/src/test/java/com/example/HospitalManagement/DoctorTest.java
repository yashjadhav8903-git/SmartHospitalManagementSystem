package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class DoctorTest {
        @Autowired
    private DoctorRepository doctorRepository;


//    @Test
//    public void testDoctors(){
//        List<Doctor> allDoctors = doctorRepository.findAllDoctors();
//        System.out.println(allDoctors);
//    }
}
