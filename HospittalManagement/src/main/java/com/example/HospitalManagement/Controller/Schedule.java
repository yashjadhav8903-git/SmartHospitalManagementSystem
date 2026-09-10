package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.SlotDTO.SlotResponseDTO;
import com.example.HospitalManagement.Entity.Doctor;
import com.example.HospitalManagement.Entity.EntityType.DoctorSchedule;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import com.example.HospitalManagement.MapStruct.SlotMapper;
import com.example.HospitalManagement.Repository.DoctorRepository;
import com.example.HospitalManagement.Repository.DoctorScheduleRepository;
import com.example.HospitalManagement.Repository.DoctorSlotRepository;
import com.example.HospitalManagement.Service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/A2/schedule")
@Tag(name = "Slot's Details")
public class Schedule {

    private final DoctorSlotRepository doctorSlotRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;
    private final SlotService slotService; // 🔥 add this
    private final SlotMapper slotMapper;


    @GetMapping("/slots")
    @Operation(summary = "get available slot's")
    public ResponseEntity<List<SlotResponseDTO>> getSlots (@RequestParam Long doctorId,
                                                         @RequestParam String date){

        log.info("Fetching slots for Doctor ID: {} on Date: {}", doctorId, date);

        List<DoctorSlot> slots = doctorSlotRepository.findByDoctorIdAndDateAndIsBookedFalse(
                 doctorId,
                LocalDate.parse(date)
        );
        List<SlotResponseDTO> dtoList = slotMapper.toDTOList(slots);
        return ResponseEntity
                .ok()
                .body(dtoList);
    }


    @GetMapping("/generate")
    @Operation(summary = "generate slot's for Appointment")
    public ResponseEntity<String> generate() {
       slotService.slotGeneration();
        return ResponseEntity.ok("Slots generated successfully 👍💓");
    }



    @PostMapping("/add-schedule")
    public ResponseEntity<String> addSchedule(@RequestParam Integer doctorId,
                                              @RequestParam DayOfWeek dayOfWeek,
                                              @RequestParam String startTime,
                                              @RequestParam String endTime) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startTime(LocalTime.parse(startTime)) // e.g., "10:00"
                .endTime(LocalTime.parse(endTime))     // e.g., "13:00"
                .isAvailable(true)
                .build();

        doctorScheduleRepository.save(schedule);
        return ResponseEntity.ok("Schedule added successfully for Doctor ID: " + doctorId);
    }
}
