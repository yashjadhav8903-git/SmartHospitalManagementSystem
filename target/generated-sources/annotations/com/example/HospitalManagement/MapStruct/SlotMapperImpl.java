package com.example.HospitalManagement.MapStruct;

import com.example.HospitalManagement.Entity.DTO.SlotDTO.SlotResponseDTO;
import com.example.HospitalManagement.Entity.EntityType.DoctorSlot;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-07T20:34:18+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class SlotMapperImpl implements SlotMapper {

    @Override
    public SlotResponseDTO EntityToDTO(DoctorSlot slot) {
        if ( slot == null ) {
            return null;
        }

        SlotResponseDTO slotResponseDTO = new SlotResponseDTO();

        if ( slot.getId() != null ) {
            slotResponseDTO.setId( slot.getId().longValue() );
        }
        slotResponseDTO.setDate( slot.getDate() );
        slotResponseDTO.setStartTime( slot.getStartTime() );
        slotResponseDTO.setEndTime( slot.getEndTime() );

        return slotResponseDTO;
    }

    @Override
    public List<SlotResponseDTO> toDTOList(List<DoctorSlot> slots) {
        if ( slots == null ) {
            return null;
        }

        List<SlotResponseDTO> list = new ArrayList<SlotResponseDTO>( slots.size() );
        for ( DoctorSlot doctorSlot : slots ) {
            list.add( EntityToDTO( doctorSlot ) );
        }

        return list;
    }
}
