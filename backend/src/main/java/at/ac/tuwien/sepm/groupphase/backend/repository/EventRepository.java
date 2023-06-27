package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Event save(Event event);

    @Transactional
    void deleteById(Long id);

    Event getEventById(Long id);

    @NonNull
    Page<Event> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Event> findAll(@NonNull Specification<Event> specification, @NonNull Pageable pageable);

    Event findById(long id);
}

