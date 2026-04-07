package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Integer> {
    List<DoctorSlot> findByDoctorIdAndDateAndIsBookedFalse(Long doctorId, LocalDate date);



}