package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRowRepository extends JpaRepository<SeatRow, Long> {
    @Query("SELECT sr FROM SeatRow sr")
    List<SeatRow> findAllSeatRowsWithHallPlans();

    Optional<SeatRow> findByRowNrAndHallPlanId(Long rowNr, Long hallPlanId);

    Optional<SeatRow> findById(Long id);

    @Query("SELECT sr FROM SeatRow sr WHERE sr.id = :seatrowId AND sr.hallPlanId = :hallplanId\n")
    Optional<SeatRow> findByIdAndHallPlanId(@Param("seatrowId") Long id, @Param("hallplanId") Long hallPlanId);

    @Query("SELECT r FROM SeatRow r "
        + "LEFT JOIN FETCH r.seats s "
        + "WHERE r.hallPlanId = :hallplanId "
        + "ORDER BY r.rowNr ASC, s.orderNr ASC")
    List<SeatRow> findAllByHallplanIdWithSeats(@Param("hallplanId") Long hallplanId);

    @Query("SELECT sr FROM SeatRow sr WHERE sr.hallPlanId = :hallplanId ORDER BY sr.rowNr ASC")
    List<SeatRow> findAllByHallplanId(@Param("hallplanId") Long hallplanId);

}

