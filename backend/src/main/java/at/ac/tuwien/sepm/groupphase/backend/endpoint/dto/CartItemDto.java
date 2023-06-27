package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public class CartItemDto {
    private SeatDto seat;
    private EventDetailDto event;
    private Long id;

    public CartItemDto() {
    }

    public CartItemDto(SeatDto seat, EventDetailDto event, Long id) {
        this.seat = seat;
        this.event = event;
        this.id = id;
    }

    public EventDetailDto getEvent() {
        return event;
    }

    public void setEvent(EventDetailDto event) {
        this.event = event;
    }

    public SeatDto getSeat() {
        return seat;
    }

    public void setSeat(SeatDto seat) {
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
