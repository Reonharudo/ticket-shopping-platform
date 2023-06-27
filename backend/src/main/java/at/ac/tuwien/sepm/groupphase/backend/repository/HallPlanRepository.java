package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallPlanRepository extends JpaRepository<HallPlan, Long> {

    @Query("SELECT c FROM HallPlan c")
    List<HallPlan> findAllHallPlans();

    @Query("SELECT h FROM HallPlan h "
        + "LEFT JOIN FETCH h.seatRows r "
        + "WHERE h.id = :hallplanId")
    Optional<HallPlan> findHallPlanById(@Param("hallplanId") Long id);

    @Query("SELECT r FROM SeatRow r "
        + "LEFT JOIN FETCH r.seats "
        + "WHERE r.hallPlanId = :hallplanId")
    Optional<HallPlan> findHallPlanByIdWithSeats(@Param("hallplanId") Long id);

    HallPlan save(HallPlan hallPlan);

    @Transactional
    void deleteById(Long id);

    @Query("SELECT s.section FROM HallPlan hp JOIN hp.seatRows sr JOIN sr.seats s WHERE hp.id = :hallPlanId")
    List<HallPlanSection> findAllSectionsByHallPlanId(@Param("hallPlanId") Long hallPlanId);

    @Query("SELECT s.section, COUNT(s.section) AS count FROM HallPlan hp JOIN hp.seatRows sr JOIN sr.seats s JOIN s.section WHERE hp.id = :hallPlanId AND s.type <> 'VACANT_SEAT' AND sr.hallPlanId = :hallPlanId GROUP BY s.section.id")
    List<Object[]> findAllSectionsByHallPlanIdCounts(@Param("hallPlanId") Long hallPlanId);

    @Query("SELECT sh, 0 AS count FROM HallPlanSection sh WHERE sh.hallPlanId = :hallPlanId")
    List<Object[]> findHallPlanCountsById(@Param("hallPlanId") Long hallPlanId);

    @Query("SELECT h FROM HallPlan h WHERE h.name ILIKE %:name% AND h.description ILIKE %:description% AND h.isTemplate = :isTemplate ")
    List<HallPlan> searchByNameAndDescriptionIgnoreCase(@Param("name") String name, @Param("description") String description, @Param("isTemplate") boolean isTemplate);

    @NonNull
    Page<HallPlan> findAll(@NonNull Specification<Event> specification, @NonNull Pageable pageable);
}
