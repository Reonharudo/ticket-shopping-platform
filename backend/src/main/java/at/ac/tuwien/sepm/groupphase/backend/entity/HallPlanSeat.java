package at.ac.tuwien.sepm.groupphase.backend.entity;


import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatStatus;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "seat")
public class HallPlanSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HallPlanSeatStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private HallPlanSeatType type;

    @NotNull
    @Column(name = "capacity", nullable = false)
    private Long capacity;
    @NotNull
    @Column(name = "seat_nr", nullable = false)
    private Long seatNr;

    @NotNull
    @Column(name = "order_nr", nullable = false)
    private Long orderNr;

    @Column(name = "seatrow_id")
    private Long seatrowId;

    @NotNull
    @Column(name = "bought_nr")
    private Long boughtNr;

    @NotNull
    @Column(name = "reserved_nr")
    private Long reservedNr;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "section_id")
    private HallPlanSection section;

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

    public Long getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(Long orderNr) {
        this.orderNr = orderNr;
    }

    public Long getSeatNr() {
        return seatNr;
    }

    public void setSeatNr(Long seatNr) {
        this.seatNr = seatNr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public HallPlanSection getSection() {
        return section;
    }

    public void setSection(HallPlanSection section) {
        this.section = section;
    }

    public Long getSeatrowId() {
        return seatrowId;
    }

    public void setSeatrowId(Long seatrowId) {
        this.seatrowId = seatrowId;
    }
}

