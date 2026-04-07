package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.SlotDTO.SlotResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.DoctorSchedule;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.MapStruct.SlotMapper;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.DoctorScheduleRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import com.example.HospitalManagement.Service.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/A2/schedule")
public class Schedule {

    private final DoctorSlotRepository doctorSlotRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final SlotService slotService; // 🔥 add this
    private final SlotMapper slotMapper;


    @GetMapping("/Slot")
    public List<SlotResponseDTO> getSlot (@RequestParam int docterId,
                                          @RequestParam String date){
        List<DoctorSlot> slots = doctorSlotRepository.findByDoctorIdAndDateAndIsBookedFalse(
                (long) docterId,
                LocalDate.parse(date)
        );
        log.info("Slot Request Received from : {}", date);
        System.out.println("Slots from DB: " + slots.size());
        List<SlotResponseDTO> dtoList = slotMapper.toDTOList(slots);
        System.out.println("DTO size: " + dtoList.size());

        return dtoList;
    }

    @GetMapping("/generate")
    public String generate() {
       slotService.slotGeneration();
        return "Slots generated 👍💓";
    }

    @PostMapping("/add-schedule")
    public String addSchedule() {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctorRepository.findById(1).get());
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(10, 0));
        schedule.setEndTime(LocalTime.of(13, 0));
        schedule.setAvailable(true);

        doctorScheduleRepository.save(schedule);

        return "Schedule added";
    }
}
