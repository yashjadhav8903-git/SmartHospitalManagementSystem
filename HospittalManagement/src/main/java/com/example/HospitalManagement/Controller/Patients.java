package com.example.HospitalManagement.Controller;

import com.example.HospitalManagement.Entity.DTO.PatientsDTO.*;
import com.example.HospitalManagement.Entity.EntityType.UserEntity;
import com.example.HospitalManagement.Projection.ForPatients.PatientSummaryPage;
import com.example.HospitalManagement.Redis.PageResponseDTO;
import com.example.HospitalManagement.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patients")
public class Patients {


    private final PatientService patientService;
    private final ModelMapper modelMapper;


    @GetMapping("/getPatients")
    public ResponseEntity<PageResponseDTO<AllPatientDTO>> getAllPatient(Pageable pageable){
        return ResponseEntity.ok((PageResponseDTO<AllPatientDTO>) patientService.getAllPatient(pageable));
    }

    @GetMapping("/patient/{patientid}")
    public ResponseEntity<AllPatientDTO> getPatientById(@PathVariable Integer patientid) throws Exception {
       UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  //--> Jo Login Patient hai wo bas apni hai patientInformation dhek payega dusro ki nahi (isni login kiya hai wo)
        return ResponseEntity.ok((AllPatientDTO) patientService.getPatientById(patientid));
    }

    // --> post Mapping
    @PostMapping("/add")
    public ResponseEntity<PatientPostResponseDTO> NewPatient(@AuthenticationPrincipal UserEntity adminUser,
                                                             @RequestBody PatientPostRequestDTO patientPostRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.NewPatient(patientPostRequestDTO,adminUser));
    }

//    // Interface Page by ID
//    @GetMapping("patients/summary/interface/page/{id}")
//    public ResponseEntity<PatientPageResponseDTO> getPatientSummaryById(@PathVariable Integer id){
//        return ResponseEntity.ok(patientService.getPatientPageById(id));
//    }

    //Projection With Interface
    @GetMapping("patients/summary/interface/projection")
    public ResponseEntity<List<PatientInterfaceProjectionDTO>> getPatientProjection(){
        return ResponseEntity.ok(patientService.getPatientSummary());
    }

    //Projection With Constructor
    @GetMapping("patients/summary/projection/constructor")
    public ResponseEntity<List<PatientResponseConstructorDTO>> getConstructorProjection(){
        return ResponseEntity.ok(patientService.getPatientConstructorSummary());
    }

    //Page with Projection(Constructor)
    @GetMapping("patients/summary/constructor/page")
    public ResponseEntity<Page<PatientPageResponseDTO>> getPageWithProjection(@RequestParam(defaultValue =  "0")int page ,
                                                                              @RequestParam(defaultValue = "2") int size ){
        Pageable pageable = PageRequest.of(page,size,Sort.by("p.id").ascending());
        return ResponseEntity.ok(patientService.getPatientWithPage(pageable));
    }

    //Page with Projection(Interface)
    @GetMapping("patients/summary/interface/page")
    public ResponseEntity<Page<PatientSummaryPage>> getPageWithPageInterface(@RequestParam(defaultValue = "0")int page,
                                                                             @RequestParam(defaultValue = "10") int size ){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(patientService.getPatientWithPageInterface(pageable));
    }

    // -->GetAllPatientWithInsuranceWithMapstruct
    @GetMapping("/mapping/patient")
    public ResponseEntity<Page<PatientInsuranceResponseDTO>> getAllPatientAndInsurance(@RequestParam(defaultValue = "0")int page,
                                                                                       @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page,size, Sort.Direction.ASC,"p.id");
        return ResponseEntity.ok(patientService.getAllPatientWithInsurance(pageable));
    }

}
