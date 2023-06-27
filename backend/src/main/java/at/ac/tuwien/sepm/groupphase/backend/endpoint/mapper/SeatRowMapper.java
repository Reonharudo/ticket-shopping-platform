package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface SeatRowMapper {

    @Named("seatRow")
    SeatRowDto toDto(SeatRow seatRow);

    @IterableMapping(qualifiedByName = "seatRow")
    List<SeatRowDto> toDto(List<SeatRow> seatRows);

    @IterableMapping(qualifiedByName = "seatRow")
    List<SeatRow> toEntity(List<SeatRowDto> seatRows);

    @Named("seatRow")
    SeatRow toEntity(SeatRowDto seatRowDto);

    default SeatRowDto mapSeatRow(SeatRow seatRow) {
        SeatRowDto seatRowDto = new SeatRowDto();
        seatRowDto.setId(seatRow.getId());
        seatRowDto.setRowNr(seatRow.getRowNr());
        seatRowDto.setHallPlanId(seatRowDto.getHallPlanId());
        seatRowDto.setSeats(mapSeats(seatRow.getSeats()));
        return seatRowDto;
    }

    default List<HallPlanSeatDto> mapSeats(List<HallPlanSeat> seats) {
        if (seats == null) {
            return Collections.emptyList();
        }
        return seats.stream()
            .map(this::mapSeat)
            .collect(Collectors.toList());
    }

    default HallPlanSectionDto mapHallPlanSection(HallPlanSection section) {
        HallPlanSectionDto hallPlanSectionDto = new HallPlanSectionDto();
        hallPlanSectionDto.setId(section.getId());
        hallPlanSectionDto.setName(section.getName());
        hallPlanSectionDto.setColor(section.getColor());
        hallPlanSectionDto.setPrice(section.getPrice());
        return hallPlanSectionDto;
    }

    default HallPlanSeatDto mapSeat(HallPlanSeat seat) {
        HallPlanSeatDto seatDto = new HallPlanSeatDto();
        seatDto.setId(seat.getId());
        seatDto.setStatus(seat.getStatus());
        seatDto.setType(seat.getType());
        seatDto.setCapacity(seat.getCapacity());
        seatDto.setSeatNr(seat.getSeatNr());
        seatDto.setSection(mapHallPlanSection(seat.getSection()));
        seatDto.setSeatrowId(seat.getSeatrowId());
        seatDto.setOrderNr(seatDto.getOrderNr());
        return seatDto;
    }

}
