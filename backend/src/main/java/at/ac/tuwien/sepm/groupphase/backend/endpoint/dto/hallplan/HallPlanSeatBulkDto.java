package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class HallPlanSeatBulkDto {

    private Long hallPlanId;

    private Long seatRowId;

    @NotNull(message = "list of seats must be provided")
    private List<HallPlanSeatDto> seats;

    public Long getHallPlanId() {
        return hallPlanId;
    }

    public void setHallPlanId(Long hallPlanId) {
        this.hallPlanId = hallPlanId;
    }

    public Long getSeatRowId() {
        return seatRowId;
    }

    public void setSeatRowId(Long seatRowId) {
        this.seatRowId = seatRowId;
    }

    public List<HallPlanSeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<HallPlanSeatDto> seats) {
        this.seats = seats;
    }
}
