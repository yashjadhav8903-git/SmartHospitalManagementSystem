package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Integer> {

    // Patient ke booking view ke liye: Unbooked slots dikhane me kaam aayega
    List<DoctorSlot> findByDoctorIdAndDateAndIsBookedFalse(Long doctorId, LocalDate date);

    // Cron Job duplicate slots na banaye, uske check ke liye:
    boolean existsByDoctorIdAndDateAndStartTime(Integer doctorId, LocalDate date, LocalTime startTime);
}


