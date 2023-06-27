package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface HallPlanSectionMapper {

    @Named("hallPlanSection")
    HallPlanSectionDto toDto(HallPlanSection section);

    @IterableMapping(qualifiedByName = "hallPlanSection")
    @Mapping(target = "count", source = "count")
    List<HallPlanSectionDto> toDto(List<HallPlanSection> section);

    HallPlanSection toEntity(HallPlanSectionDto sectionDto);
}
