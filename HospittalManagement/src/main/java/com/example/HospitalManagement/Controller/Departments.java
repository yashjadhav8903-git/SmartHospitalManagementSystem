package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.Entity.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.Entity.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/departments")
public class Departments {

    private final DepartmentService departmentService;

    @PostMapping("/assign-Department/{doctorId}")
    public ResponseEntity<DoctorResponseDTO> assignDepartmentToDoctor(@PathVariable Integer doctorId ,
                                                                      @RequestBody AssignDepartmentRequestDTO assignDepartmentRequestDTO){

        return ResponseEntity.ok(departmentService.assignDoctor(doctorId,assignDepartmentRequestDTO));
    }


    //--> findDoctorByDepartmentId
    @GetMapping("/departmentId/{id}")
    public ResponseEntity<DepartmentResponseDeptDTO> getDepartmentAndDoctorById (@PathVariable Integer id,
                                                                                 Pageable pageable){
        return ResponseEntity.ok(departmentService.getDepartmentAndDoctorById(id,pageable));
    }

    //--> Find All Department
//    @GetMapping("/getDepartments")
//    public ResponseEntity<Page<DepartmentNoIDResponseDTO>> getAllDepartment(Pageable pageable){
//        return ResponseEntity.ok((Page<DepartmentNoIDResponseDTO>) departmentService.getAllDepartment(pageable));
//    }

    //--> Find All Department (Same) with Mapstruct
    @GetMapping("/getDepartments")
    public ResponseEntity<Page<DepartmentNoIDResponseDTO>> getAllDepartment(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC,"d.id");
        return ResponseEntity.ok(departmentService.getAllDepartment(pageable)
        );
    }
}


