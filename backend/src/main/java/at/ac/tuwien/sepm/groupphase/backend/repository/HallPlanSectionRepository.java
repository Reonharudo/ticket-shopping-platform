package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallPlanSectionRepository extends JpaRepository<HallPlanSection, Long> {

    @Query("SELECT s FROM HallPlanSection s WHERE s.hallPlanId = :hallplanId")
    List<HallPlanSection> findAllByHallplanId(@Param("hallplanId") Long hallplanId);

}

