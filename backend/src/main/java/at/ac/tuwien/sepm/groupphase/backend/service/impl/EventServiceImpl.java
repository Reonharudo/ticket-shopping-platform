package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventDate;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventDateRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.Predicate;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final HallPlanRepository hallPlanRepository;
    private final EventDateRepository eventDateRepository;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, HallPlanRepository hallPlanRepository, EventDateRepository eventDateRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.hallPlanRepository = hallPlanRepository;
        this.eventDateRepository = eventDateRepository;
    }

    @Override
    public Event create(EventDetailDto event) throws ValidationException {
        LOG.trace("create({})", event);
        Optional<Event> existingEvent;
        if (event.getId() != null) {
            existingEvent = eventRepository.findById(event.getId());
            if (existingEvent.isPresent()) {
                throw new ValidationException("Event with id:" + event.getId() + " already exists");
            }
        }

        for (EventDateDto ed : event.getEventDatesLocation()) {
            Optional<HallPlan> existingHallplan = hallPlanRepository.findById(ed.getRoom());
            if (existingHallplan.isEmpty()) {
                throw new ValidationException("Event can not find place in the Hallplan with id:" + ed.getRoom() + ","
                    + " because it does not exist");
            }
        }
        Event debug = eventMapper.eventDetailDtoToEvent(event);
        debug.setSoldTickets(0L);
        return eventRepository.save(debug);
    }

    @Override
    public Page<Event> findAllPagesByDate(int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, 20, Sort.by("title").ascending());

        LOG.debug("Find all event entries by pageable: {}", pageable);
        return eventRepository.findAll(pageable);
    }

    @Override
    public Page<Event> findAllPagesByDateAndAuthorAndLocation(int pageIndex, LocalDate fromDate, LocalDate toDate, String artist, String location, String titleCategory, LocalTime startingTime, LocalTime duration) {
        Pageable pageable = PageRequest.of(pageIndex, 20, Sort.by("title").ascending());
        String eventDatesLocation = "eventDatesLocation";
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.isTrue(root.get("id"));
            if (fromDate != null) {
                predicate = criteriaBuilder.greaterThanOrEqualTo(root.join(eventDatesLocation).get("date"), fromDate);
            }

            if (toDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.join(eventDatesLocation).get("date"), toDate));
            }

            if (artist != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("artist")), "%" + artist.toLowerCase() + "%"));
            }

            if (location != null) {
                Predicate inner = criteriaBuilder.or(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.join(eventDatesLocation).get("city")), "%" + location.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.join(eventDatesLocation).get("address")), "%" + location.toLowerCase() + "%")),
                    criteriaBuilder.like(criteriaBuilder.lower(root.join(eventDatesLocation).get("areaCode").as(String.class)), "%" + location.toLowerCase() + "%"));
                predicate = criteriaBuilder.and(predicate, inner);
            }

            if (titleCategory != null) {
                Predicate inner = criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + titleCategory.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + titleCategory.toLowerCase() + "%"));
                predicate = criteriaBuilder.and(predicate, inner);
            }

            if (startingTime != null) {
                LocalTime upper = startingTime.plusMinutes(30);
                LocalTime lower = startingTime.minusMinutes(30);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.join(eventDatesLocation).get("startingTime"), lower));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.join(eventDatesLocation).get("startingTime"), upper));
            }

            if (duration != null) {
                LocalTime upper = duration.plusMinutes(30);
                LocalTime lower = duration.minusMinutes(30);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), lower));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("duration"), upper));
            }

            return predicate;
        };
        return eventRepository.findAll(specification, pageable);
    }

    @Override
    public Page<Event> getTopEvent() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("soldTickets").descending());
        return eventRepository.findAll(pageable);
    }

    @Override
    public EventDetailDto getEventById(Long id) throws NotFoundException {
        LOG.trace("getEventById({})", id);
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new NotFoundException("Event with id: " + id + " can not be found!");
        }
        Event event = eventRepository.getEventById(id);
        return eventMapper.eventToEventDetailDto(event);
    }

    @Override
    public EventDetailDto getEventFromHallplanId(Long hallplanId) {
        EventDate eventDate = eventDateRepository.getEventDateByRoom(hallplanId);
        Event event = eventRepository.getEventById(eventDate.getEvent());

        List<EventDate> eventDateList = new ArrayList<>();
        eventDateList.add(eventDate);
        event.setEventDatesLocation(eventDateList);
        return eventMapper.eventToEventDetailDto(event);
    }

    @Override
    public PerformanceDto getPerformanceFromHallplanId(Long hallplanId) {
        EventDate eventDate = eventDateRepository.getEventDateByRoom(hallplanId);
        Event event = eventRepository.getEventById(eventDate.getEvent());
        return new PerformanceDto(event, eventDate);
    }

    @Override
    public Page<Event> findPageByTitleSubstring(String searchString, int number) {

        Pageable pageable = PageRequest.of(0, number, Sort.by("title").ascending());
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.isNotNull(root.get("id"));
            if (searchString != null) {
                predicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
            }
            return predicate;
        };
        return eventRepository.findAll(specification, pageable);
    }

    @Override
    @Lock(LockModeType.OPTIMISTIC)
    public void incrementSoldTickets(Long hallplanId) {
        EventDetailDto eventDetailDto = this.getEventFromHallplanId(hallplanId);
        Event event = eventRepository.getEventById(eventDetailDto.getId());
        event.setSoldTickets(event.getSoldTickets() + 1);
        eventRepository.saveAndFlush(event);
    }

    @Override
    @Lock(LockModeType.OPTIMISTIC)
    public void decrementSoldTickets(Long hallplanId) {
        EventDetailDto eventDetailDto = this.getEventFromHallplanId(hallplanId);
        Event event = eventRepository.getEventById(eventDetailDto.getId());
        event.setSoldTickets(event.getSoldTickets() - 1);
        eventRepository.saveAndFlush(event);
    }

}
