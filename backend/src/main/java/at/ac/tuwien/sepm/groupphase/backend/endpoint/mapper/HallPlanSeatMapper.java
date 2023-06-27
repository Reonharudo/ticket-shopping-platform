package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HallPlanSeatMapper {

    @Named("seat")
    @Mapping(target = "section", source = "section")
    HallPlanSeat toEntity(HallPlanSeatDto hallPlanSeatDto);

    @Named("seat")
    @Mapping(target = "section", source = "section")
    List<HallPlanSeat> toEntity(List<HallPlanSeatDto> hallPlanSeatDto);

    @Named("seat")
    @Mapping(target = "section", source = "section")
    HallPlanSeatDto toDto(HallPlanSeat hallPlanSeat);

    @Named("seat")
    @Mapping(target = "section", source = "section")
    List<HallPlanSeatDto> toDto(List<HallPlanSeat> hallPlanSeat);


}
