package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventDate;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EventTest implements TestData {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    HallPlanRepository hallPlanRepository;
    Event event;

    @BeforeEach
    public void setUp() {


        //setUp hallplans
        HallPlan hallPlan1 = new HallPlan();
        hallPlan1.setName("Main Hall");
        hallPlan1.setDescription("This is a Main Hall with over 500 Seats.");
        hallPlan1 = hallPlanRepository.save(hallPlan1);

        //setUp eventDates
        EventDate event1Date1 = new EventDate();
        EventDate event1Date2 = new EventDate();
        EventDate event1Date3 = new EventDate();

        //give them valid parameters
        event1Date1.setDate(LocalDate.parse("2023-05-21"));
        event1Date1.setAddress("Landstraße 11");
        event1Date1.setRoom(hallPlan1.getId());
        event1Date1.setAreaCode(1111);
        event1Date1.setCity("Vienna");
        event1Date1.setStartingTime(LocalTime.parse("12:02:00"));
        List<EventDate> eventDates = new ArrayList<>();
        eventDates.add(event1Date1);

        event = new Event();
        event.setTitle("Main Event");
        event.setDuration(LocalTime.parse("02:15:00"));
        event.setArtist("Elvis");
        event.setCategory("Concert");
        event.setEventDatesLocation(eventDates);
    }
    @Test
    public void testSaveValidEvent() {
        Event savedEvent = eventRepository.save(event);
        assertNotNull(savedEvent.getId());
    }

    @Test
    public void testSaveInvalidArtistEventThrows() throws ConstraintViolationException {
        event.setArtist("");
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
    }

    @Test
    public void testSaveInvalidEventDateThrows() throws ConstraintViolationException {
        event.getEventDatesLocation().get(0).setStartingTime(null);
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
    }



    @Test
    public void testSaveInvalidEventRoomThrows() throws ConstraintViolationException {
        hallPlanRepository.deleteById(1L);
        event.getEventDatesLocation().get(0).setStartingTime(null);
        assertThrows(ConstraintViolationException.class, () -> eventRepository.save(event));
    }


    @Test
    public void testDeleteEventByValidId() {
        Event savedEvent = eventRepository.save(event);
        Long eventId = savedEvent.getId();

        eventRepository.deleteById(eventId);
        Optional<Event> deletedEvent = eventRepository.findById(eventId);
        assertTrue(deletedEvent.isEmpty());
    }

    @Test
    public void testGetEventById() {
        Event savedEvent = eventRepository.save(event);
        Long eventId = savedEvent.getId();

        Optional<Event> retrievedEvent = eventRepository.findById(eventId);

        assertNotNull(retrievedEvent);
        assertTrue(retrievedEvent.isPresent());
        assertEquals(event.getTitle(), retrievedEvent.get().getTitle());
    }
}
