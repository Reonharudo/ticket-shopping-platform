package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatStatus;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class HallPlanSeatDto {

    private Long id;

    @NotNull(message = "status must be specified")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "status must be specified")
    private HallPlanSeatStatus status;

    @NotNull(message = "type must be specified")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "type must be specified")
    private HallPlanSeatType type;

    @NotNull(message = "capacity must be specified")
    @Min(value = 1, message = "capacity must be greater than or equal to {value}")
    private Long capacity;

    @NotNull(message = "seatNr must be specified")
    @Min(value = 1, message = "seatNr must be greater than or equal to {value}")
    private Long seatNr;

    @NotNull(message = "orderNr must be specified")
    private Long orderNr;

    @Valid
    private HallPlanSectionDto section;

    @NotNull(message = "The number of purchases for this seat must be given!")
    @Min(value = 0, message = "bought_nr must be greater than or equal to {value}")
    private Long boughtNr;

    @NotNull(message = "The number of purchases for this seat must be given!")
    @Min(value = 0, message = "reserved_nr must be greater than or equal to {value}")
    private Long reservedNr;

    private Long seatrowId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(Long orderNr) {
        this.orderNr = orderNr;
    }

    public Long getCapacity() {
        return capacity;
    }

    public Long getBoughtNr() {
        return boughtNr;
    }

    public void setBoughtNr(Long boughtNr) {
        this.boughtNr = boughtNr;
    }

    public Long getReservedNr() {
        return reservedNr;
    }

    public void setReservedNr(Long reservedNr) {
        this.reservedNr = reservedNr;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getSeatNr() {
        return seatNr;
    }

    public void setSeatNr(Long seatNr) {
        this.seatNr = seatNr;
    }

    public HallPlanSectionDto getSection() {
        return section;
    }

    public void setSection(HallPlanSectionDto section) {
        this.section = section;
    }

    public Long getSeatrowId() {
        return seatrowId;
    }

    public void setSeatrowId(Long seatrowId) {
        this.seatrowId = seatrowId;
    }

    public HallPlanSeatStatus getStatus() {
        return status;
    }

    public void setStatus(HallPlanSeatStatus status) {
        this.status = status;
    }

    public HallPlanSeatType getType() {
        return type;
    }

    public void setType(HallPlanSeatType type) {
        this.type = type;
    }

    public static final class SeatDtoBuilder {

        private Long id;
        private HallPlanSeatStatus status;
        private HallPlanSeatType type;
        private Long capacity;
        private Long seatNr;
        private Long orderNr;

        private SeatDtoBuilder() {
        }

        public static SeatDtoBuilder aSeatDto() {
            return new SeatDtoBuilder();
        }

        public SeatDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SeatDtoBuilder withStatus(HallPlanSeatStatus status) {
            this.status = status;
            return this;
        }

        public SeatDtoBuilder withType(HallPlanSeatType type) {
            this.type = type;
            return this;
        }

        public SeatDtoBuilder withCapacity(Long capacity) {
            this.capacity = capacity;
            return this;
        }

        public SeatDtoBuilder withSeatNr(Long seatNr) {
            this.seatNr = seatNr;
            return this;
        }

        public SeatDtoBuilder withOrderNr(Long orderNr) {
            this.orderNr = orderNr;
            return this;
        }


        public HallPlanSeatDto build() {
            HallPlanSeatDto seatDto = new HallPlanSeatDto();
            seatDto.setId(id);
            seatDto.setStatus(status);
            seatDto.setType(type);
            seatDto.setCapacity(capacity);
            seatDto.setSeatNr(seatNr);
            seatDto.setOrderNr(orderNr);
            //seatDto.setHa
            return seatDto;
        }
    }
}

