package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
import at.ac.tuwien.sepm.groupphase.backend.service.SeatRowService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallPlanSeatService seatService;
    private final SeatRowService rowService;
    private final EventService eventService;

    public TicketServiceImpl(HallPlanSeatService seatService, SeatRowService rowService, EventService eventService) {
        this.seatService = seatService;
        this.rowService = rowService;
        this.eventService = eventService;
    }

    @Override
    public TicketDto ticketDtoFromTicket(Ticket ticket) {
        LOGGER.debug("Create ticketDto From ticket of seat {}", ticket.getSeatId());
        HallPlanSeatDto hallPlanSeatDto = seatService.getSeatById(ticket.getSeatId());
        SeatRowDto rowDto = rowService.getSeatRowById(hallPlanSeatDto.getSeatrowId());

        EventDetailDto eventDetailDto = eventService.getEventFromHallplanId(rowDto.getHallPlanId());

        SeatDto seatDto = new SeatDto(hallPlanSeatDto, rowDto);
        TicketDto ticketDto = new TicketDto();
        ticketDto.setTicketNr(ticket.getTicketNr());
        ticketDto.setSeat(seatDto);
        ticketDto.setEvent(eventDetailDto);
        return ticketDto;
    }
}
