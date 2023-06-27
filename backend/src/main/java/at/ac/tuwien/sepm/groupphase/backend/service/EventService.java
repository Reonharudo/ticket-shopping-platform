package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import jakarta.persistence.LockModeType;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventService {
    /**
     * Creates a event with the given attributes.
     *
     * @param event the event to create
     * @return the created event
     */
    Event create(EventDetailDto event) throws ValidationException;

    /**
     * Fetches the Event with the corresponding id.
     *
     * @param id the id of the event
     * @return the found EventDetailDto or NULL if the event doesn't exist
     */
    EventDetailDto getEventById(Long id);

    /**
     * Finds all pages of events sorted by Date.
     *
     * @param pageIndex index of page to load
     * @return page of max 20 events sorted by date
     */
    Page<Event> findAllPagesByDate(int pageIndex);

    /**
     * Finds all pages of events with the filters given.
     *
     * @param pageIndex     index of page to load
     * @param fromDate      the earliest date that is searched
     * @param toDate        the latest data that is searched
     * @param author        the author of given event
     * @param location      the location that is searched for
     * @param titleCategory the title or category that is searched for
     * @param startingTime  the start time that is searched for
     * @param duration      the duration that is searched for
     * @return page of max 20 events sorted by date
     */
    Page<Event> findAllPagesByDateAndAuthorAndLocation(int pageIndex, LocalDate fromDate, LocalDate toDate, String author,
                                                       String location, String titleCategory, LocalTime startingTime, LocalTime duration);


    /**
     * Fetches the Event that has an Eventdate with the given hallplanId.
     *
     * @param hallplanId the id of the hallplan
     * @return returns the EventDetailDto corresponding to the hallplanId
     */
    EventDetailDto getEventFromHallplanId(Long hallplanId);

    /**
     * Finds the top 10 sold out events.
     *
     * @return page of max 10 events sorted by date
     */
    Page<Event> getTopEvent();

    PerformanceDto getPerformanceFromHallplanId(Long hallplanId);

    /**
     * Finds a pages of events by a substring of the title.
     *
     * @param searchString the partial string that should be matched in the title of the event
     * @param number       the maximum number of returned events
     * @return page of max {@code number} events sorted alphabetically ascending
     */
    Page<Event> findPageByTitleSubstring(String searchString, int number);

    void incrementSoldTickets(Long hallplanId);

    @Lock(LockModeType.OPTIMISTIC)
    void decrementSoldTickets(Long hallplanId);


}
