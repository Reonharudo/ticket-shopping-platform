package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallPlanSeatRepository extends JpaRepository<HallPlanSeat, Long> {

    List<HallPlanSeat> findAllBySeatrowId(Long seatRow);

    List<HallPlanSeat> findAllBySection(Long section);

    @Query("SELECT s FROM HallPlanSeat s WHERE s.seatrowId = :seatRowId AND s.seatNr = :seatNr")
    List<HallPlanSeat> findAllBySeatRowIdAndSeatNr(@Param("seatRowId") Long seatRowId, @Param("seatNr") Long seatNr);

    @Query("SELECT s FROM HallPlanSeat s JOIN FETCH s.section WHERE s.id = :id")
    Optional<HallPlanSeat> getSeatById(@Param("id") Long id);
}
