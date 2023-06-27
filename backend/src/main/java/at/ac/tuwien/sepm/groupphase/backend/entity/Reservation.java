package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationNr;
    private Long userId;
    private LocalDate date;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) //mappedBy = "reservation",
    @JoinColumn(name = "reservationNr")
    private List<ReservationSeat> reservationSeatList;

    public Long getReservationNr() {
        return reservationNr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ReservationSeat> getReservationSeatsList() {
        return reservationSeatList;
    }

    public void setReservationSeatsList(List<ReservationSeat> reservationSeatList) {
        this.reservationSeatList = reservationSeatList;
    }
}
