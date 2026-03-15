package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.EntityType.Doctor;
import com.example.HospitalManagement.Projection.ForDoctors.DoctorProjectionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {

//    @Query(value = "select * from Doctor",nativeQuery = true)
//    List<Doctor> findAllDoctors();

    // --> Find By ID
    Page<DoctorProjectionView> findById(Integer id , Pageable pageable);



    // --> All Doctor
//    @Query(value = "select * from Doctor",nativeQuery = true)
    Page<DoctorProjectionView> findAllProjectedBy(Pageable pageable);
}
