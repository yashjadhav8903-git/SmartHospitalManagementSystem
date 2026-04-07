package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.Entity.EntityType.DoctorSchedule;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.Repository.DoctorScheduleRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SlotService {

   private final DoctorScheduleRepository doctorScheduleRepository;
   private final DoctorSlotRepository doctorSlotRepository;

    @Scheduled(cron = "0 0 0 * * ? ")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void slotGeneration() {
        System.out.println("SLOT GENERATION STARTED");

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            List<DoctorSchedule> scheduleList = doctorScheduleRepository.findByDayOfWeekAndIsAvailableTrue(dayOfWeek);
            System.out.println("Schedules found: " + scheduleList.size());


            for (DoctorSchedule schedule : scheduleList) {
                LocalTime start = schedule.getStartTime();

                while (start.isBefore(schedule.getEndTime())) {
                    DoctorSlot slot = new DoctorSlot();
                    slot.setDoctor(schedule.getDoctor());
                    slot.setDate(date);
                    slot.setStartTime(start);
                    slot.setEndTime(start.plusMinutes(30));
//                    slot.setBooked(false);
                    doctorSlotRepository.save(slot); // 🔥 THIS WAS MISSING
                    start = start.plusMinutes(30); // 🔥 infinite loop fix
                }
            }
        }
    }
}
