package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan,Long> {

}
