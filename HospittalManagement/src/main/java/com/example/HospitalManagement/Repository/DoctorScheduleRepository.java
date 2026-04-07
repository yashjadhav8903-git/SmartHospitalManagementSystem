package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.EntityType.DoctorSchedule;
import java.time.DayOfWeek;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Integer> {
    List<DoctorSchedule> findByDayOfWeekAndIsAvailableTrue(DayOfWeek dayOfWeek);

}