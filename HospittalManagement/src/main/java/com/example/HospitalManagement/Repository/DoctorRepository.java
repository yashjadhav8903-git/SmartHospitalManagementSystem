package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Projection.ForDoctors.DoctorProjectionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {

    boolean existsByUserEntityId(Integer userId);

    // --> Find By ID
    Page<DoctorProjectionView> findById(Integer id , Pageable pageable);
}
