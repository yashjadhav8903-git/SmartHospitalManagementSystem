package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.SlotDTO.SlotResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SlotMapper {

    SlotResponseDTO EntityToDTO(DoctorSlot slot);
    List<SlotResponseDTO> toDTOList(List<DoctorSlot> slots);

}
