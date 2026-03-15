package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.DTO.BloodGroupCountResponseEntity;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPageResponseDTO;
import com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientResponseConstructorDTO;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.ForPatients.PatientInsuranceProjection;
import com.example.HospitalManagement.Projection.ForPatients.PatientSummaryPage;
import com.example.HospitalManagement.Projection.ForPatients.PatientSummaryProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {


    // PatientRepository.java
    boolean existsByUserEntityId(Integer userId);

    //--> 1.Find By Name
//   Patient findByName(String name);

    //--> 2.Find By BirthDate And Email
//   List<Patient> findByBirthdateOrEmail(LocalDate birthDate, String email);

//    List<Patient> FindBirthDateBetween(LocalDate startDate,LocalDate endDate);

    // --> 3.FindNameContainingOrderById
//    List<Patient> findByNameContainingOrderByIdDesc(String Query);



    // --> Manual Query
    //--> Find patient by BloodGroup
//    @Query("select p from Patient p where p.BloodGroup = ?1")
////    @Query("select p from Patient p where p.BloodGroup = BloodGroup")
//    List<Patient> findByBloodGroup(@Param( "BloodGroup") Blood_Group_type BloodGroup);

//
//    //--> FindByBornAfterDate
//    @Query("select p from Patient p where p.birthdate > :birthdate")
//    List<Patient> FindByBornAfterDate(@Param("birthdate")LocalDate birthdate);


    //--> FindCountOfBloodGroup
//    @Query("select p.BloodGroup, Count (p) from Patient p group by p.BloodGroup")
//    List<Object[]> findCountEachBloodGroupType();

//
//    --> Native Query
//     --> FindAllPatient
    @Query(value = "select * from Patient", nativeQuery = true)
//    List<Patient> findAllPatient();

    //--> Pagination
    Page<Patient> findAllPatient(Pageable pageable);
    Page<Patient>findById(Integer id,Pageable pageable);


    // --> update Name
    @Transactional //---> transactional Required in Update Query
    @Modifying
    @Query("update Patient p set p.name =:name where p.id =:id")
    int UpdateNameWithId(@Param("name") String name,@Param("id") Integer id);


    //--> Projection
    @Query("select new com.example.HospitalManagement.Entity.DTO.BloodGroupCountResponseEntity(p.BloodGroup,"
                    + " COUNT(p)) from Patient p group by p.BloodGroup")
    List<BloodGroupCountResponseEntity> countEachBloodGroupType();



    //Interface Projection
    @Query("""
                 select p.id as id,
                    p.name as name,
                    p.gender as gender,
                    p.email as email,
                    COUNT(a) as appointmentCount from Patient p
                    left join p.appointments a
                 group by p.id,p.name,p.gender,p.email
            """)
    List<PatientSummaryProjection> findPatientSummary(Sort id);



    //Constructor Projection
    @Query("""
                 select new com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientResponseConstructorDTO
                 ( p.id as id,
                    p.name as name,
                    p.gender as gender,
                    p.email as email,
                    COUNT(a) as appointmentCount ) from Patient p
                    left join p.appointments a
                 group by p.id,p.name,p.gender,p.email
            """)
    List<PatientResponseConstructorDTO> findPatientSummaryConstructor(Sort id);



    //Page with Constructor Projection
    @Query("""
            select new com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPageResponseDTO
                ( p.id as id,
                p.name as name,
                p.email as email,
                p.gender as gender,
                COUNT(a) as appointmentsCount ) from Patient p
                left join p.appointments a
                group by p.id,p.name,p.gender,p.email
            """)
    Page<PatientPageResponseDTO> findPatientWithPage(Pageable pageable);


    //Page with Constructor Projection by id
//    @Query("""
//            select new com.example.HospitalManagement.Entity.DTO.PatientsDTO.PatientPageResponseDTOO
//                ( p.id,
//                p.name,
//                p.email,
//                p.gender,
//                COUNT(a)
//                )
//                 from Patient p
//                left join p.appointments a
//                where p.id =:id
//                group by p.id,p.name,p.gender,p.email
//            """)
//    PatientPageResponseDTO findPatientWithPageById(@Param("id") Integer id);


    //Page with Interface projection
    @Query("""
                 select p.id as id,
                    p.name as name,
                    p.gender as gender,
                    p.email as email,
                    COUNT(a) as appointmentCount from Patient p
                    left join p.appointments a
                 group by p.id,p.name,p.gender,p.email
            """)
    Page<PatientSummaryPage> findPatientWithPageInterface(Pageable pageable);



    // // -->GetAllPatientWithInsuranceWithMapstruct
    @Query("""
            select p.id as id,
            p.name as name,
            p.email as email,
            i.provider as provider,
            i.insuranceType as insuranceType,
            i.validUntil as validUntil,
            i.createdAt as createdAt
            from Patient p
            left join p.insurance i
            """)
    Page<PatientInsuranceProjection> getAllPatientWithInsurance(Pageable pageable);


}
