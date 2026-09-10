package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Department;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Integer> {

    Optional<Department> findByNameIgnoreCase(String departmentNames);

    boolean existsByNameIgnoreCase(String name);

    // department by ID
    @Query("""
            select d.id as id,
            d.name as departmentNames,
            d.headDoctor.name as headDoctorName
            from Department d
            where d.id =:id
            """)
    Optional<DepartmentProjectionDTO> findDepartmentProjectionById(@Param("id") Integer id);

    // Doctor from that Department (Pageable)
    @Query("""
            select doc.id as id,
            doc.name as name,
            doc.specialization as specialization,
            doc.email as email
            from Department d
            join d.doctors doc
            where d.id =:id
            """)
    Page<DoctorProjectionDTO> findDoctorByDepartmentId(@Param("id") Integer id, Pageable pageable);


    // -->GetAllDepartment
    @Query("""
            select d.id as id,
            d.name as departmentNames,
            d.headDoctor.name as headDoctorName
            from Department d
            """)
    Page<DepartmentProjectionDTO> findAllDepartmentProjections(Pageable pageable);
}
