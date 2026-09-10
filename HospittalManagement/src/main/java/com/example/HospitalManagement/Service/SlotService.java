package com.example.HospitalManagement.Service;

import com.example.HospitalManagement.Entity.EntityType.DoctorSchedule;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.Repository.DoctorScheduleRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class SlotService {

   private final DoctorScheduleRepository doctorScheduleRepository;
   private final DoctorSlotRepository doctorSlotRepository;

   // Trigger Point schedule Cron Structure ---> (Second = 0 | Minute = 0 | Houes = 0 | Day Of Month = * | Month = * | Day of Week = *)
    @Scheduled(cron = "0 0 0 * * ? ")
    public void slotGeneration() {
        System.out.println("SLOT GENERATION STARTED");



        // Aaj ka Day/Date find kro
        LocalDate today = LocalDate.now();

        List<DoctorSlot> newSlots = new ArrayList<>();
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
                    LocalTime slotEndTime = start.plusMinutes(30);

                    // Pehle check karo ki ye slot already generate toh nahi ho chuka
                    boolean alreadyExists = doctorSlotRepository.existsByDoctorIdAndDateAndStartTime(
                            schedule.getDoctor().getId(),
                            date,
                            start
                    );

                    if (!alreadyExists) {
                        DoctorSlot slot = DoctorSlot.builder()
                                .doctor(schedule.getDoctor())
                                .date(date)
                                .startTime(start)
                                .endTime(slotEndTime)
                                .isBooked(false)
                                .build();


                        newSlots.add(slot);
                    }

                    // Increment 30 mins
                    start = slotEndTime;

                }
            }
        }
        // Single batch save call
        if (!newSlots.isEmpty()) {
            doctorSlotRepository.saveAll(newSlots);
            System.out.println("Successfully generated " + newSlots.size() + " new slots!");
        } else {
            log.warn("All slots for the next 7 days already exist.");
        }
    }
}
