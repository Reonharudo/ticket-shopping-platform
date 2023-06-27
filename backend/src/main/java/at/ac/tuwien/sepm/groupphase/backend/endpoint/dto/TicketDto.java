package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class TicketDto {
    private Long ticketNr;
    private SeatDto seat;
    private EventDetailDto event;

    public TicketDto() {
    }

    public Long getTicketNr() {
        return ticketNr;
    }

    public void setTicketNr(Long ticketNr) {
        this.ticketNr = ticketNr;
    }

    public SeatDto getSeat() {
        return seat;
    }

    public void setSeat(SeatDto seat) {
        this.seat = seat;
    }

    public EventDetailDto getEvent() {
        return event;
    }

    public void setEvent(EventDetailDto event) {
        this.event = event;
    }
}
