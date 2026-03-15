package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.EntityType.Department;
import com.example.HospitalManagement.Projection.ForDepartments.DepartmentProjectionDTO;
import com.example.HospitalManagement.Projection.ForDepartments.DoctorProjectionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department,Integer> {

    Optional<Department> findByDepartmentNames(String departmentNames);

    // department by ID
    @Query("""
            select d.id as id,
            d.departmentNames as departmentNames,
            d.headDoctor.name as headDoctorName
            from Department d
            where d.id =:id
            """)
    DepartmentProjectionDTO findDepartmentById(Integer id);

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
    Page<DoctorProjectionDTO> findDoctorByDepartmentId(Integer id, Pageable pageable);


    // -->GetAllDepartment
    @Query("""
            select d.id as id,
            d.departmentNames as departmentNames,
            d.headDoctor.name as headDoctorName
            from Department d
            """)
    Page<DepartmentProjectionDTO> findAllDepartment(Pageable pageable);
}
