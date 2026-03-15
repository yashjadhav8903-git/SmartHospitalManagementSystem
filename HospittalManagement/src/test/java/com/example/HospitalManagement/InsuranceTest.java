package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.Insurance;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Service.InsuranceService;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

//@SpringBootTest
//public class InsuranceTest {
//
//    @Autowired
//    private InsuranceService insuranceService;
//
////    @Test
////    public void testInsurance(){
////        Insurance insurance = Insurance.builder()
////                .policyNumber("HDFC")
////                .provider("HDFC Heath Department")
////                .validUntil(LocalDate.of(2030,12,10))
////                .build();
////
////        Patient patient = insuranceService.assignInsurancePatient(insurance,1);
////        System.out.println(patient);
//
//        //Remove only Insurance from Patient Not a Patient
////        Patient Newpatient = insuranceService.RemoveInsuranceFromPatient(patient.getId());
////        System.out.println(Newpatient);
//    }
//}