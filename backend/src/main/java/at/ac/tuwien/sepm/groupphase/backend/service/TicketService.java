package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;

public interface TicketService {

    /**
     * Creates a TicketDto from a given Ticket.
     *
     * @param ticket the Ticket to be converted
     * @return the built TicketDto
     */
    TicketDto ticketDtoFromTicket(Ticket ticket);
}
