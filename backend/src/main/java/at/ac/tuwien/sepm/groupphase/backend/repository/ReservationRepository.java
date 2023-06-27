package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findReservationsByUserIdOrderByReservationNrDesc(Long id);

    @Override
    Reservation save(Reservation reservation);

    void deleteReservationByReservationNr(Long reservationNr);

    Reservation findReservationByReservationNr(Long reservationNr);

}
