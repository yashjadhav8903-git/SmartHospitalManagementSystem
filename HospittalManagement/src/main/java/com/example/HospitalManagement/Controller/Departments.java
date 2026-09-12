package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentNoIDResponseDTO;
import com.example.HospitalManagement.DTO.DepartmentsDTO.DepartmentResponseDeptDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.AssignDepartmentRequestDTO;
import com.example.HospitalManagement.DTO.DoctorsDTO.Departments.DoctorResponseDTO;
import com.example.HospitalManagement.Service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/departments")
@Tag(name = "Department-API's")
public class Departments {

    private final DepartmentService departmentService;

    @PostMapping("/assign-Department/{doctorId}")
    @Operation(summary = "assign department to doctor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> assignDepartmentToDoctor(@PathVariable Integer doctorId ,
                                                                      @RequestBody AssignDepartmentRequestDTO assignDepartmentRequestDTO){

        return ResponseEntity.ok(departmentService.assignDoctor(doctorId,assignDepartmentRequestDTO));
    }


    //--> findDoctorByDepartmentId
    @GetMapping("/{id}")
    @Operation(summary = "find Doctor By Department-Id")
    @PreAuthorize("hasAuthority('Department:Operations')")
    public ResponseEntity<DepartmentResponseDeptDTO> getDepartmentAndDoctorById (@PathVariable Integer id,
                                                                                 Pageable pageable){
        return ResponseEntity.ok(departmentService.getDepartmentAndDoctorById(id,pageable));
    }


    //--> Find All Department (Same) with Mapstruct
    @GetMapping
    @Operation(summary = "find all department")
    @PreAuthorize("hasAuthority('Department:Operations')")
    public ResponseEntity<Page<DepartmentNoIDResponseDTO>> getAllDepartment(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC,"d.id");
        return ResponseEntity.ok(departmentService.getAllDepartment(pageable));


    }
}


