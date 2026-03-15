package com.example.HospitalManagement;

import com.example.HospitalManagement.Entity.DTO.BloodGroupCountResponseEntity;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
public class PatientTest {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientService patientService;

    @Test
    public void TestPatientRepository(){
//        List<com.example.HospitalManagement.Entity.Patient> patientList = patientRepository.findAll();
//        System.out.println(patientList);
   }

    @Test
    public void testTransactionMethod() {

//        com.example.HospitalManagement.Entity.Patient patient = patientService.getPatientById(Math.toIntExact(2L));
//        System.out.println(patient);


        //--> 1.Find By Name
//        com.example.HospitalManagement.Entity.Patient patient1 = patientRepository.findByName("Mayra Yadav");
//        System.out.println(patient1);


//        //--> 2.Find By BirthDate And Email
//        List<com.example.HospitalManagement.Entity.Patient> patientList = patientRepository.
//                            findByBirthdateOrEmail(LocalDate.of(1998,5,1),"MayraYadav@gmail.com");
//
//        for(com.example.HospitalManagement.Entity.Patient patient2 : patientList){
//            System.out.println(patient2);
//        }


        // --> 3.FindNameContainingOrderById
//        List<com.example.HospitalManagement.Entity.Patient> patientList = patientRepository.
//                            findByNameContainingOrderByIdDesc("Ya");
//
//        for(com.example.HospitalManagement.Entity.Patient patient3 : patientList){
//            System.out.println(patient3);
//        }



        //--> 4.Find patient by BloodGroup
//       List<com.example.HospitalManagement.Entity.Patient> patientList4 = patientRepository
//                                    .findByBloodGroup(Blood_Group_type.O_POSITIVE);

//        for(com.example.HospitalManagement.Entity.Patient patient4 : patientList4){
//            System.out.println(patient4);
//        }



        //---> 5.FindByBornAfterDate
//        List<com.example.HospitalManagement.Entity.Patient> patientList5 = patientRepository
//                .FindByBornAfterDate(LocalDate.of(2004,12,8));
//
//        for(com.example.HospitalManagement.Entity.Patient patient4 : patientList5){
//            System.out.println(patient4);
//        }

        //--->//--> FindCountOfBloodGroup
//        List<Object[]> BloodGroupList = patientRepository.findCountEachBloodGroupType();
//
//        for(Object[] objects : BloodGroupList){
//            System.out.println(objects[0]+ " " + objects[1]);
//        }


        //--> native Query
        //--> FindAllPatient
//        Page<com.example.HospitalManagement.Entity.Patient> patientList = patientRepository.findAllPatient();
//        System.out.println(patientList);


        //--> Update Query
        int rowsUpdated = patientRepository.UpdateNameWithId("Pooja Parihar",5);
        System.out.println("How Many Rows was Affected :-> " + rowsUpdated);



        //--> pagination
//        Page<Patient> patientList = patientRepository
//                .findAllPatient(PageRequest.of(0,5, Sort.by("name")));
//        for(com.example.HospitalManagement.Entity.Patient patient0 : patientList){
//            System.out.println(patient0);
//        }
//


//        ---> Projection blood group count
        List<BloodGroupCountResponseEntity> BloodGroupList = patientRepository.countEachBloodGroupType();
        for(BloodGroupCountResponseEntity bloodGroupCountResponseEntity : BloodGroupList){
            System.out.println(bloodGroupCountResponseEntity);
        }
    }
}
