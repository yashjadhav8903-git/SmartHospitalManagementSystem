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

   // Trigger Point schedule Cron Structure ---> (Second = 0 | Minute = 0 | Houes = 0 | Day Of Month = * | Month = * | Day of Week = *)
    @Scheduled(cron = "0 0 0 * * ? ")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void slotGeneration() {
        System.out.println("SLOT GENERATION STARTED");



        // Aaj ka Day/Date find kro
        LocalDate today = LocalDate.now();
        // Aaj se 7 din tak chalega
        for (int i = 0; i < 7; i++) {
            // Ek ek din aaje bado
            LocalDate date = today.plusDays(i);
            // us din konsa Day tha (ex .Monday or sunday)
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            // us din konsa doctor Available hai
            List<DoctorSchedule> scheduleList = doctorScheduleRepository.findByDayOfWeekAndIsAvailableTrue(dayOfWeek);
            System.out.println("Schedules found: " + scheduleList.size());

            // usko fetch kro (wo lege aao)
            for (DoctorSchedule schedule : scheduleList) {
                // us doctor ko start time assign kro
                LocalTime start = schedule.getStartTime();

                // ye loop start se endtime tak chalega
                while (start.isBefore(schedule.getEndTime())) {
                    // Empty slot
                    DoctorSlot slot = new DoctorSlot();
                    // fullfill that slot
                    slot.setDoctor(schedule.getDoctor());
                    slot.setDate(date);
                    slot.setStartTime(start);
                    // EndTime start se 30 minute ka rahega
                    slot.setEndTime(start.plusMinutes(30));
                    // save that slot
                    doctorSlotRepository.save(slot); // 🔥 THIS WAS MISSING
                    // next slot 30 minute baad hi start hoga
                    start = start.plusMinutes(30); // 🔥 infinite loop fix
                }
            }
        }
    }
}
