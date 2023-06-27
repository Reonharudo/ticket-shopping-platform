package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class ReservationDto {
    private Long reservationNr;
    private EventDetailDto event;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;
    private List<SeatDto> reservedSeats;

    public ReservationDto() {

    }

    public ReservationDto(Long reservationNr, EventDetailDto event, LocalDate reservationDate, List<SeatDto> reservedSeats) {
        this.reservationNr = reservationNr;
        this.event = event;
        this.reservationDate = reservationDate;
        this.reservedSeats = reservedSeats;
    }

    public EventDetailDto getEvent() {
        return event;
    }

    public void setEvent(EventDetailDto event) {
        this.event = event;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public List<SeatDto> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(List<SeatDto> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Long getReservationNr() {
        return reservationNr;
    }

    public void setReservationNr(Long reservationNr) {
        this.reservationNr = reservationNr;
    }
}
