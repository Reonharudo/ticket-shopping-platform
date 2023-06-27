package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;

import java.util.List;

public class HallPlanSeatRowBulkDto {

    private Long hallPlanId;
    private List<SeatRowDto> seatRows;

    public List<SeatRowDto> getSeatRows() {
        return seatRows;
    }

    public void setSeatRows(List<SeatRowDto> seatRows) {
        this.seatRows = seatRows;
    }

    public Long getHallPlanId() {
        return hallPlanId;
    }

    public void setHallPlanId(Long hallPlanId) {
        this.hallPlanId = hallPlanId;
    }
}
